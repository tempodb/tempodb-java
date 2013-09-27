package com.tempodb;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.*;
import org.apache.http.auth.*;
import org.apache.http.client.*;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.*;
import org.apache.http.protocol.*;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.tempodb.json.Json;
import static com.tempodb.util.Preconditions.*;

/**
 *  The main object used to make calls to the TempoDB api.
 *
 *  It is a thin wrapper around the <a target="_blank" href="http://tempo-db.com/docs/api/">TempoDB Rest API</a>
 *
 *  <p>A client object holds the session information required to authenticate and connect to the Rest api. An api key and secret
 *  are required. These can be obtained by signing up at <a href="http://tempo-db.com">http://tempo-db.com</a>. The client
 *  also lets you specify a different hostname (for instance, if you are on a dedicated cluster), the port, and whether to
 *  use SSL encryption on the connection.
 *
 *  <p>Using the client, you can:
 *  <ul>
 *    <li>Retrieve a filtered list of Series</li>
 *    <li>Retrieve a Series by key</li>
 *    <li>Retrieve datapoints for a single series in a specific time interval</li>
 *    <li>Write datapoints for a single series</li>
 *    <li>Retrieve datapoints aggregated across multiple Series</li>
 *    <li>Write datapoints to multiple Series</li>
 *  </ul>
 *
 *  <p>The following example initializes a Client object and retrieves datapoints for a Series referenced by the key "my-key"
 *  for the time period <tt>2012-01-01</tt> to <tt>2010-01-02</tt>, returning the hourly average. This calls returns a <tt>Cursor&lt;DataPoint&gt;</tt>
 *  which provides a lazily loaded iterable of DataPoints.
 *
 *  <p><pre>
 *    import org.joda.time.DateTime;
 *    import org.joda.time.DateTimeZone;
 *    import org.joda.time.Interval;
 *    import org.joda.time.Period;
 *
 *    Client client = new Client("api-key", "api-secret", "api.tempo-db.com", 443, true);
 *
 *    DateTime start = new DateTime(2012, 1, 1, 0, 0, 0, 0);
 *    DateTime end = new DateTime(2012, 1, 2, 0, 0, 0, 0);
 *    Rollup rollup = new Rollup(Period.hours(1), Fold.MEAN);
 *
 *    Cursor&lt;DataPoint&gt; datapoints = client.readDataPointsByKey("my-key", new Interval(start, end), rollup, DateTimeZone.UTC);
 *  </pre>
 *
 *  <p>The TempoDB Rest API supports http keep-alive, and the Client object is designed to be thread-safe. It is recommended
 *  that a Client object be created and then reused for subsequent calls. This help to amoritize the cost of setting up the
 *  http client across many calls.
 */
public class Client {

  private final String key;
  private final String secret;
  private final String host;
  private final int port;
  private final boolean secure;

  private HttpClient client = null;
  private HttpHost target = null;

  private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
  // Timeout on milliseconds
  private static final int DEFAULT_TIMEOUT_MILLIS = 50000;  // 50 seconds
  private static final long DEFAULT_KEEPALIVE_TIMEOUT_MILLIS = 50000;  // 50 seconds
  private static final int GENERIC_ERROR_CODE = 600;
  private static final String VERSION = "1.0-SNAPSHOT";
  private static final String API_VERSION = "v1";
  private final DateTimeFormatter iso8601 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

  private enum HttpMethod { GET, POST, PUT, DELETE }

  /**
   *  Base constructor for a Client object.
   *
   *  @param key Api key
   *  @param secret Api secret
   *  @param host Hostname of the api server
   *  @param port Port that the api server is listening on
   *  @param secure Uses http if false, https if true
   */
  public Client(String key, String secret, String host, int port, boolean secure) {
    this.key = key;
    this.secret = secret;
    this.host = host;
    this.port = port;
    this.secure = secure;
  }

  /**
   *  Deletes a range of datapoints for a Series specified by key.
   *
   *  @param key The series key
   *  @param interval The start/end datetime interval to delete.
   *  @return Nothing
   *
   *  @since 1.0.0
   */
  public Result<Nothing> deleteDataPointsByKey(String key, Interval interval) {
    checkNotNull(key);
    checkNotNull(interval);

    URI uri = null;
    try {
      URIBuilder builder = new URIBuilder(String.format("/%s/series/key/%s/data/", API_VERSION, key));
      addIntervalToURI(builder, interval);
      uri = builder.build();
    } catch (URISyntaxException e) {
      String message = String.format("Could not build URI with inputs: key: %s, interval: %s", key, interval);
      throw new IllegalArgumentException(message, e);
    }

    HttpRequest request = buildRequest(uri.toString(), HttpMethod.DELETE);
    Result<Nothing> result = execute(request, Nothing.class);
    return result;
  }

  /**
   *  Returns a cursor of datapoints specified by series key.
   *
   *  @param key The series key
   *  @param interval An interval of time for the query (start/end datetimes)
   *  @param rollup The rollup for the read query. This can be null.
   *  @param timezone The time zone for the returned datapoints.
   *  @return A Cursor of DataPoints. The cursor.iterator().next() may throw a {@link TempoDBException} if an error occurs while making a request.
   *
   *  @see Cursor
   *  @since 1.0.0
   */
  public Cursor<DataPoint> readDataPointsByKey(String key, Interval interval, Rollup rollup, DateTimeZone timezone) {
    checkNotNull(interval);
    checkNotNull(timezone);

    URI uri = null;
    try {
      URIBuilder builder = new URIBuilder(String.format("/%s/series/key/%s/data/segment/", API_VERSION, key));
      addIntervalToURI(builder, interval);
      addRollupToURI(builder, rollup);
      addTimeZoneToURI(builder, timezone);
      uri = builder.build();
    } catch (URISyntaxException e) {
      String message = String.format("Could not build URI with inputs: key: %s, interval: %s, rollup: %s, timezone: %s", key, interval, rollup, timezone);
      throw new IllegalArgumentException(message, e);
    }

    Cursor<DataPoint> cursor = new DataPointCursor(uri, this);
    return cursor;
  }

  /**
   *  Returns a cursor of datapoints specified by a series filter.
   *
   *  This endpoint allows one to request multiple series and apply an aggregation function.
   *
   *  @param filter The series filter
   *  @param interval An interval of time for the query (start/end datetimes)
   *  @param aggregation The aggregation for the read query. This is required.
   *  @param rollup The rollup for the read query. This can be null.
   *  @param timezone The time zone for the returned datapoints.
   *  @return A Cursor of DataPoints. The cursor.iterator().next() may throw a {@link TempoDBException} if an error occurs while making a request.
   *
   *  @see Aggregation
   *  @see Cursor
   *  @see Filter
   *  @see Rollup
   *  @since 1.0.0
   */
  public Cursor<DataPoint> readDataPointsByFilter(Filter filter, Interval interval, Aggregation aggregation, Rollup rollup, DateTimeZone timezone) {
    checkNotNull(filter);
    checkNotNull(interval);
    checkNotNull(aggregation);
    checkNotNull(timezone);

    URI uri = null;
    try {
      URIBuilder builder = new URIBuilder(String.format("/%s/data/segment/", API_VERSION));
      addFilterToURI(builder, filter);
      addIntervalToURI(builder, interval);
      addAggregationToURI(builder, aggregation);
      addRollupToURI(builder, rollup);
      addTimeZoneToURI(builder, timezone);
      uri = builder.build();
    } catch (URISyntaxException e) {
      String message = String.format("Could not build URI with inputs: filter: %s, interval: %s, aggregation: %s, rollup: %s, timezone: %s", filter, interval, aggregation, rollup, timezone);
      throw new IllegalArgumentException(message, e);
    }

    Cursor<DataPoint> cursor = new DataPointCursor(uri, this);
    return cursor;
  }

  /**
   *  Deletes a Series referenced by key.
   *
   *  @param key The Series key to delete
   *  @return {@link Nothing}
   *  @since 1.0.0
   */
  public Result<Nothing> deleteSeriesByKey(String key) {
    checkNotNull(key);

    URI uri = null;
    try {
      URIBuilder builder = new URIBuilder(String.format("/%s/series/key/%s/", API_VERSION, key));
      uri = builder.build();
    } catch (URISyntaxException e) {
      String message = String.format("Could not build URI with inputs: key: %s", key);
      throw new IllegalArgumentException(message, e);
    }

    HttpRequest request = buildRequest(uri.toString(), HttpMethod.DELETE);
    Result<Nothing> result = execute(request, Nothing.class);
    return result;
  }

  /**
   *  Returns a Series referenced by key.
   *
   *  @param key The Series key to retrieve
   *  @return The requested Series.
   *  @since 1.0.0
   */
  public Result<Series> getSeriesByKey(String key) {
    checkNotNull(key);

    URI uri = null;
    try {
      URIBuilder builder = new URIBuilder(String.format("/%s/series/key/%s/", API_VERSION, key));
      uri = builder.build();
    } catch (URISyntaxException e) {
      String message = String.format("Could not build URI with inputs: key: %s", key);
      throw new IllegalArgumentException(message, e);
    }

    HttpRequest request = buildRequest(uri.toString());
    Result<Series> result = execute(request, Series.class);
    return result;
  }

  /**
   *  Deletes set of series by a filter.
   *
   *  @param filter The series filter @see Filter
   *  @return A DeleteSummary providing information about the series deleted.
   *
   *  @see DeleteSummary
   *  @see Filter
   *  @since 1.0.0
   */
  public Result<DeleteSummary> deleteSeriesByFilter(Filter filter) {
    URI uri = null;
    try {
      URIBuilder builder = new URIBuilder(String.format("/%s/series/", API_VERSION));
      addFilterToURI(builder, filter);
      uri = builder.build();
    } catch (URISyntaxException e) {
      String message = String.format("Could not build URI with input - filter: %s", filter);
      throw new IllegalArgumentException(message, e);
    }

    HttpRequest request = buildRequest(uri.toString(), HttpMethod.DELETE);
    Result<DeleteSummary> result = execute(request, DeleteSummary.class);
    return result;
  }

  /**
   *  Deletes all Series in a database.
   *
   *  @return A DeleteSummary providing information about the series deleted.
   *
   *  @see DeleteSummary
   *  @since 1.0.0
   */
  public Result<DeleteSummary> deleteAllSeries() {
    URI uri = null;
    try {
      URIBuilder builder = new URIBuilder(String.format("/%s/series/", API_VERSION));
      builder.addParameter("allow_truncation", "true");
      uri = builder.build();
    } catch (URISyntaxException e) {
      String message = "Could not build URI";
      throw new IllegalArgumentException(message, e);
    }

    HttpRequest request = buildRequest(uri.toString(), HttpMethod.DELETE);
    Result<DeleteSummary> result = execute(request, DeleteSummary.class);
    return result;
  }

  /**
   *  Returns a cursor of series specified by a filter.
   *
   *  @param filter The series filter
   *  @return A Cursor of Series. The cursor.iterator().next() may throw a {@link TempoDBException} if an error occurs while making a request.
   *
   *  @see Cursor
   *  @see Filter
   *  @since 1.0.0
   */
  public Cursor<Series> getSeriesByFilter(Filter filter) {
    URI uri = null;
    try {
      URIBuilder builder = new URIBuilder(String.format("/%s/series/", API_VERSION));
      addFilterToURI(builder, filter);
      uri = builder.build();
    } catch (URISyntaxException e) {
      String message = String.format("Could not build URI with input - filter: %s", filter);
      throw new IllegalArgumentException(message, e);
    }

    Cursor<Series> cursor = new SeriesCursor(uri, this);
    return cursor;
  }

  /**
   *  Replaces all of a Series metadata
   *
   *  @param series The series to replace
   *  @return The updated Series
   *
   *  @see Series
   *  @since 1.0.0
   */
  public Result<Series> replaceSeries(Series series) {
    URI uri = null;
    try {
      URIBuilder builder = new URIBuilder(String.format("/%s/series/key/%s/", API_VERSION, series.getKey()));
      uri = builder.build();
    } catch (URISyntaxException e) {
      String message = "Could not build URI";
      throw new IllegalArgumentException(message, e);
    }

    Result<Series> result = null;
    String body = null;
    try {
      body = Json.dumps(series);
    } catch (JsonProcessingException e) {
      String message = "Error serializing the body of the request. More detail: " + e.getMessage();
      result = new Result<Series>(null, GENERIC_ERROR_CODE, message);
      return result;
    }

    HttpRequest request = buildRequest(uri.toString(), HttpMethod.PUT, body);
    result = execute(request, Series.class);
    return result;
  }

  /**
   *  Writes datapoints to multiple Series.
   *
   *  <p>This request can partially succeed. You should check the {@link Result#getState()} to check if the request was
   *  successful. If the request was partially successful, the result's {@link MultiStatus} can be inspected to determine
   *  what failed.
   *
   *  @param data List of MultiDataPoint's to write
   *  @return {@link Nothing}
   *
   *  @see MultiDataPoint
   *  @see MultiStatus
   *  @since 1.0.0
   */
  public Result<Nothing> writeDataPoints(List<MultiDataPoint> data) {
    checkNotNull(data);

    URI uri = null;
    try {
      URIBuilder builder = new URIBuilder(String.format("/%s/multi/", API_VERSION));
      uri = builder.build();
    } catch (URISyntaxException e) {
      String message = "Could not build URI.";
      throw new IllegalArgumentException(message, e);
    }

    Result<Nothing> result = null;
    String body = null;
    try {
      body = Json.dumps(data);
    } catch (JsonProcessingException e) {
      String message = "Error serializing the body of the request. More detail: " + e.getMessage();
      result = new Result<Nothing>(null, GENERIC_ERROR_CODE, message);
      return result;
    }

    HttpRequest request = buildRequest(uri.toString(), HttpMethod.POST, body);
    result = execute(request, Nothing.class);
    return result;
  }


  /**
   *  Writes datapoints to single Series, referenced by key.
   *
   *  @param key The key of the series to write to.
   *  @param data A list of datapoints
   *  @return {@link Nothing}
   *
   *  @see DataPoint
   *  @see Nothing
   *  @since 1.0.0
   */
  public Result<Nothing> writeDataPointsByKey(String key, List<DataPoint> data) {
    checkNotNull(key);
    checkNotNull(data);

    URI uri = null;
    try {
      URIBuilder builder = new URIBuilder(String.format("/%s/series/key/%s/data/", API_VERSION, key));
      uri = builder.build();
    } catch (URISyntaxException e) {
      String message = String.format("Could not build URI with inputs: key: %s", key);
      throw new IllegalArgumentException(message, e);
    }

    Result<Nothing> result = null;
    String body = null;
    try{
      body = Json.dumps(data);
    } catch (JsonProcessingException e) {
      String message = "Error serializing the body of the request. More detail: " + e.getMessage();
      result = new Result<Nothing>(null, GENERIC_ERROR_CODE, message);
      return result;
    }

    HttpRequest request = buildRequest(uri.toString(), HttpMethod.POST, body);
    result = execute(request, Nothing.class);
    return result;
  }

  private void addFilterToURI(URIBuilder builder, Filter filter) {
    if(filter != null) {
      for(String key : filter.getKeys()) {
        builder.addParameter("key", key);
      }

      for(String tag : filter.getTags()) {
        builder.addParameter("tag", tag);
      }

      for(Map.Entry<String, String> attribute : filter.getAttributes().entrySet()) {
        builder.addParameter(String.format("attr[%s]", attribute.getKey()), attribute.getValue());
      }
    }
  }

  private void addIntervalToURI(URIBuilder builder, Interval interval) {
    if(interval != null) {
      builder.addParameter("start", interval.getStart().toString(iso8601));
      builder.addParameter("end", interval.getEnd().toString(iso8601));
    }
  }

  private void addAggregationToURI(URIBuilder builder, Aggregation aggregation) {
    if(aggregation != null) {
      builder.addParameter("aggregation.fold", aggregation.getFold().toString().toLowerCase());
    }
  }

  private void addRollupToURI(URIBuilder builder, Rollup rollup) {
    if(rollup != null) {
      builder.addParameter("rollup.period", rollup.getPeriod().toString());
      builder.addParameter("rollup.fold", rollup.getFold().toString().toLowerCase());
    }
  }

  private void addTimeZoneToURI(URIBuilder builder, DateTimeZone timezone) {
    if(timezone != null) {
      builder.addParameter("tz", timezone.toString());
    }
  }

  HttpRequest buildRequest(String uri) {
    return buildRequest(uri, HttpMethod.GET, null);
  }

  HttpRequest buildRequest(String uri, HttpMethod method) {
    return buildRequest(uri, method, null);
  }

  HttpRequest buildRequest(String uri, HttpMethod method, String body) {
    HttpRequest request = null;

    switch(method) {
      case POST:
        HttpPost post = new HttpPost(uri);
        if(body != null) {
          post.setEntity(new StringEntity(body, DEFAULT_CHARSET));
        }
        request = post;
        break;
      case PUT:
        HttpPut put = new HttpPut(uri);
        if(body != null) {
          put.setEntity(new StringEntity(body, DEFAULT_CHARSET));
        }
        request = put;
        break;
      case DELETE:
        request = new HttpDelete(uri);
        break;
      case GET:
      default:
        request = new HttpGet(uri);
        break;
    }

    return request;
  }

  HttpResponse execute(HttpRequest request) throws IOException {
    HttpClient client = getHttpClient();
    HttpContext context = getContext();
    HttpHost target = getTarget();
    HttpResponse response = client.execute(target, request, context);
    return response;
  }

  <T> Result<T> execute(HttpRequest request, Class<T> klass) {
    Result<T> result = null;
    try {
      HttpResponse response = execute(request);
      result = new Result<T>(response, klass);
    } catch (IOException e) {
      result = new Result<T>(null, GENERIC_ERROR_CODE, e.getMessage());
    }
    return result;
  }

  private synchronized HttpClient getHttpClient() {
    if(client == null) {
      HttpParams httpParams = new BasicHttpParams();
      HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_TIMEOUT_MILLIS);
      HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_TIMEOUT_MILLIS);
      HttpProtocolParams.setUserAgent(httpParams, String.format("tempodb-java/%s", getVersion()));

      DefaultHttpClient defaultClient = new DefaultHttpClient(new PoolingClientConnectionManager(), httpParams);
      defaultClient.getCredentialsProvider().setCredentials(
          new AuthScope(host, port),
          new UsernamePasswordCredentials(key, secret));

      // Add gzip header to all requests
      defaultClient.addRequestInterceptor(new HttpRequestInterceptor() {
        public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
          if (!request.containsHeader("Accept-Encoding")) {
            request.addHeader("Accept-Encoding", "gzip");
          }
        }
      });

      defaultClient.addResponseInterceptor(new HttpResponseInterceptor() {
        public void process(final HttpResponse response, final HttpContext context) throws HttpException, IOException {
          HttpEntity entity = response.getEntity();
          if (entity != null) {
            Header ceheader = entity.getContentEncoding();
            if (ceheader != null) {
              HeaderElement[] codecs = ceheader.getElements();
              for (int i = 0; i < codecs.length; i++) {
                if (codecs[i].getName().equalsIgnoreCase("gzip")) {
                  response.setEntity(new GzipDecompressingEntity(response.getEntity()));
                  return;
                }
              }
            }
          }
        }
      });

      defaultClient.setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {
        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
          HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));

          while(it.hasNext()) {
            HeaderElement he = it.nextElement();
            String param = he.getName();
            String value = he.getValue();
            if(value != null && param.equalsIgnoreCase("timeout")) {
              try {
                return Long.parseLong(value) * 1000;
              } catch (NumberFormatException ignore) {
              }
            }
          }
          return DEFAULT_KEEPALIVE_TIMEOUT_MILLIS;
        }
      });

      client = defaultClient;
    }
    return client;
  }

  synchronized Client setHttpClient(HttpClient httpClient) {
    this.client = httpClient;
    return this;
  }

  private HttpContext getContext() {
    HttpHost targetHost = getTarget();

    // Create AuthCache instance
    AuthCache authCache = new BasicAuthCache();
    // Generate BASIC scheme object and add it to the local
    // auth cache
    BasicScheme basicAuth = new BasicScheme();
    authCache.put(targetHost, basicAuth);

    // Add AuthCache to the execution context
    BasicHttpContext localcontext = new BasicHttpContext();
    localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);
    return localcontext;
  }

  private HttpHost getTarget() {
    if(target == null) {
      String scheme = secure ? "https" : "http";
      target = new HttpHost(host, port, scheme);
    }
    return target;
  }

  private String getVersion() {
    return VERSION;
  }
}
