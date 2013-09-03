package com.tempodb;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.*;
import org.apache.http.auth.*;
import org.apache.http.client.*;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.*;
import org.apache.http.protocol.*;


public class Client {

  private final String key;
  private final String secret;
  private final String host;
  private final int port;
  private final boolean secure;

  private HttpClient client = null;
  private HttpHost target = null;

  // Timeout on milliseconds
  private static final int DEFAULT_TIMEOUT_MILLIS = 50000;  // 50 seconds
  private static final String VERSION = "1.0-SNAPSHOT";
  private static final String API_VERSION = "v1";

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

  private HttpRequest buildRequest(String url, HttpMethod method) throws UnsupportedEncodingException {
    return buildRequest(url, method, null);
  }

  private HttpRequest buildRequest(String url, HttpMethod method, String body) throws UnsupportedEncodingException {
    HttpRequest request = null;
    String uri = String.format("/%s%s", API_VERSION, url);

    switch(method) {
      case POST:
        HttpPost post = new HttpPost(uri);
        if(body != null) {
          post.setEntity(new StringEntity(body));
        }
        request = post;
      case PUT:
        HttpPut put = new HttpPut(uri);
        if(body != null) {
          put.setEntity(new StringEntity(body));
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

  public HttpResponse execute(HttpRequest request) throws IOException {
    HttpClient client = getHttpClient();
    HttpHost target = getTarget();
    HttpResponse response = client.execute(target, request);
    return response;
  }

  private synchronized HttpClient getHttpClient() {
    if(client == null) {
      HttpParams httpParams = new BasicHttpParams();
      HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_TIMEOUT_MILLIS);
      HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_TIMEOUT_MILLIS);
      HttpProtocolParams.setUserAgent(httpParams, String.format("tempodb-java/%s", getVersion()));

      DefaultHttpClient defaultClient = new DefaultHttpClient(new ThreadSafeClientConnManager(), httpParams);
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
