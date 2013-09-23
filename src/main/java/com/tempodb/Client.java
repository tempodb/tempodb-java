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
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.*;
import org.apache.http.protocol.*;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.tempodb.json.Json;
import static com.tempodb.util.Preconditions.*;


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
  private static final int GENERIC_ERROR_CODE = 600;
  private static final String VERSION = "1.0-SNAPSHOT";
  private static final String API_VERSION = "v1";
  private final DateTimeFormatter iso8601 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

  private enum HttpMethod { GET, POST, PUT, DELETE }

  /**
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
   *  Returns a cursor of datapoints specified by series key.
   *
   *  @param key The series key
   *  @param interval An interval of time for the query (start/end datetimes) @see org.joda.time.Iterval
   *  @param rollup The rollup for the read query. This can be null. @see Rollup
   *  @param timezone The time zone for the returned datapoints. @see org.joda.time.DateTimeZone
   *  @return A Cursor of DataPoints. @see Cursor The @{link java.util.Iterator#next next} may throw a @{link TempoDBApiException}
   *          if an error occurs while making a request.
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
   *  This endpoint allows one to request multiple series and apply an aggregation function.
   *
   *  @param filter The series filter @see Filter
   *  @param interval An interval of time for the query (start/end datetimes) @see org.joda.time.Iterval
   *  @param aggregation The aggregation for the read query. This is required. @see Aggregation
   *  @param rollup The rollup for the read query. This can be null. @see Rollup
   *  @param timezone The time zone for the returned datapoints. @see org.joda.time.DateTimeZone
   *  @return A Cursor of DataPoints. @see Cursor The @{link java.util.Iterator#next next} may throw a @{link TempoDBApiException}
   *          if an error occurs while making a request.
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
   *  @return The Series to delete.
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
   *  Returns a cursor of series specified by a filter.
   *
   *  @param filter The series filter @see Filter
   *  @return A Cursor of Series. @see Cursor The @{link java.util.Iterator#next next} may throw a @{link TempoDBApiException}
   *          if an error occurs while making a request.
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
   *  Writes datapoints
   *
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
      result = new Result(null, GENERIC_ERROR_CODE, message);
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

  protected HttpRequest buildRequest(String uri) {
    return buildRequest(uri, HttpMethod.GET, null);
  }

  protected HttpRequest buildRequest(String uri, HttpMethod method) {
    return buildRequest(uri, method, null);
  }

  protected HttpRequest buildRequest(String uri, HttpMethod method, String body) {
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

  protected HttpResponse execute(HttpRequest request) throws IOException {
    HttpClient client = getHttpClient();
    HttpHost target = getTarget();
    HttpResponse response = client.execute(target, request);
    return response;
  }

  protected <T> Result<T> execute(HttpRequest request, Class<T> klass) {
    Result<T> result = null;
    try {
      HttpResponse response = execute(request);
      result = new Result(response, klass);
    } catch (IOException e) {
      result = new Result(null, GENERIC_ERROR_CODE, e.getMessage());
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

      client = defaultClient;
    }
    return client;
  }

  private HttpHost getTarget() {
    if(target == null) {
      String scheme = secure ? "https" : "http";
      target = new HttpHost(host, port, scheme);
    }
    return target;
  }

  public synchronized Client setHttpClient(HttpClient httpClient) {
    this.client = httpClient;
    return this;
  }

  private String getVersion() {
    return VERSION;
  }
}
