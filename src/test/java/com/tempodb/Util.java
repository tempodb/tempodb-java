package com.tempodb;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.*;
import org.mockito.Mockito;


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
    HttpClient mockHttpClient = Mockito.mock(HttpClient.class);
    Mockito.when(mockHttpClient.execute(Mockito.any(HttpHost.class), Mockito.any(HttpRequest.class))).thenReturn(response);
    return mockHttpClient;
  }

  public static HttpResponse getResponse(int expectedStatus, String expectedBody) {
    ProtocolVersion version = new ProtocolVersion("HTTP", 1, 1);
    StatusLine statusLine = new BasicStatusLine(version, expectedStatus, "");
    HttpResponse response = new BasicHttpResponse(statusLine);
    response.setStatusCode(expectedStatus);
    response.setEntity(new StringEntity(expectedBody, Charset.forName("UTF-8")));
    return response;
  }
}
