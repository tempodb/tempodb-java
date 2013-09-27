package com.tempodb;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.apache.http.message.*;
import org.apache.http.protocol.HttpContext;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;


public class Util {

  public static Client getClient(HttpClient httpClient) {
    Client client = new Client("key", "secret", "example.com", 80, false);
    client.setHttpClient(httpClient);
    return client;
  }

  public static Client getClient(HttpResponse response) throws IOException {
    HttpClient mockClient = getMockHttpClient(response);
    Client client = getClient(mockClient);
    return client;
  }

  public static HttpClient getMockHttpClient(HttpResponse response) throws IOException {
    HttpClient mockHttpClient = mock(HttpClient.class);
    when(mockHttpClient.execute(any(HttpHost.class), any(HttpRequest.class), any(HttpContext.class))).thenReturn(response);
    return mockHttpClient;
  }

  public static HttpClient getMockHttpClient(HttpResponse response, HttpResponse... responses) throws IOException {
    HttpClient mockHttpClient = mock(HttpClient.class);
    when(mockHttpClient.execute(any(HttpHost.class), any(HttpRequest.class), any(HttpContext.class))).thenReturn(response, responses);
    return mockHttpClient;
  }

  public static HttpResponse getResponse(int expectedStatus, String expectedBody) {
    ProtocolVersion version = new ProtocolVersion("HTTP", 1, 1);
    StatusLine statusLine = new BasicStatusLine(version, expectedStatus, EnglishReasonPhraseCatalog.INSTANCE.getReason(expectedStatus, Locale.US));
    HttpResponse response = new BasicHttpResponse(statusLine);
    response.setEntity(new StringEntity(expectedBody, Charset.forName("UTF-8")));
    return response;
  }

  public static HttpRequest captureRequest(HttpClient client) throws IOException {
    ArgumentCaptor<HttpRequest> argument = ArgumentCaptor.forClass(HttpRequest.class);
    verify(client).execute(any(HttpHost.class), argument.capture(), any(HttpContext.class));
    return argument.getValue();
  }
}
