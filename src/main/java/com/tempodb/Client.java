package com.tempodb;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Iterator;

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
   *  Returns an iterator of datapoints specified by series id.
   *
   *  @param id The series id
   *  @param interval An interval of time for the query (start/end datetimes) @see org.joda.time.Iterval
   *  @param rollup The rollup for the read query. This can be null. @see Rollup
   *  @param timezone The time zone for the returned datapoints. @see org.joda.time.DateTimeZone
   *  @return An Iterator of DataPoints. The @{link java.util.Iterator#next next} may throw a @{link TempoDBApiException}
   *          if an error occurs while making a request.
   *  @throws TempoDBApiException {@link TempoDBApiException} if an error occurs retrieving the first batch of datapoints
   */
  public Iterator<DataPoint> readDataPointsById(String id, Interval interval, Rollup rollup, DateTimeZone timezone) throws TempoDBException {
    return readDataPointsOne("id", id, interval, rollup, timezone);
  }

  /**
   *  Returns an iterator of datapoints specified by series key.
   *
   *  @param key The series key
   *  @param interval An interval of time for the query (start/end datetimes) @see org.joda.time.Iterval
   *  @param rollup The rollup for the read query. This can be null. @see Rollup
   *  @param timezone The time zone for the returned datapoints. @see org.joda.time.DateTimeZone
   *  @return An Iterator of DataPoints. The @{link java.util.Iterator#next next} may throw a @{link TempoDBApiException}
   *          if an error occurs while making a request.
   *  @throws TempoDBApiException {@link TempoDBApiException} if an error occurs retrieving the first batch of datapoints
   */
  public Iterator<DataPoint> readDataPointsByKey(String key, Interval interval, Rollup rollup, DateTimeZone timezone) throws TempoDBException {
    return readDataPointsOne("key", key, interval, rollup, timezone);
  }

  private Iterator<DataPoint> readDataPointsOne(String type, String value, Interval interval, Rollup rollup, DateTimeZone timezone) throws TempoDBException {
    checkNotNull(interval);
    checkNotNull(timezone);

    URI uri = null;
    try {
      URIBuilder builder = new URIBuilder(String.format("/%s/series/%s/%s/data/segment/", API_VERSION, type, value));
      addIntervalToURI(builder, interval);
      addRollupToURI(builder, rollup);
      addTimeZoneToURI(builder, timezone);
      uri = builder.build();
    } catch (URISyntaxException e) {
      String message = String.format("Could not build URI with inputs: %s: %s, interval: %s, rollup: %s, timezone: %s", type, value, interval, rollup, timezone);
      throw new IllegalArgumentException(message, e);
    }

    HttpRequest request = buildRequest(uri.toString(), HttpMethod.GET);
    Result<DataPointSegment> result = execute(request, DataPointSegment.class);
    Cursor<DataPoint> cursor = null;
    if(result.isSuccessful()) {
      SegmentIterator<DataPointSegment> segments = new SegmentIterator(this, result.getValue(), DataPointSegment.class);
      cursor = new Cursor(segments);
    } else {
      throw new TempoDBException(result.getMessage(), result.getCode());
    }
    return cursor;
  }

  private void addIntervalToURI(URIBuilder builder, Interval interval) {
    if(interval != null) {
      builder.addParameter("start", interval.getStart().toString(iso8601));
      builder.addParameter("end", interval.getEnd().toString(iso8601));
    }
  }

  private void addRollupToURI(URIBuilder builder, Rollup rollup) {
    if(rollup != null) {
      builder.addParameter("rollup.period", rollup.getPeriod().toString());
      builder.addParameter("rollup.fold", rollup.getFold().toString());
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
