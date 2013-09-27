package com.tempodb;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.junit.*;
import static org.junit.Assert.*;


public class DeleteSeriesByKeyTest {

  @Test
  public void smokeTest() throws IOException {
    HttpResponse response = Util.getResponse(200, "");
    Client client = Util.getClient(response);

    Result<Nothing> expected = new Result<Nothing>(new Nothing(), 200, "OK");
    Result<Nothing> result = client.deleteSeriesByKey("key1");
    assertEquals(expected, result);
  }

  @Test
  public void testMethod() throws IOException {
    HttpResponse response = Util.getResponse(200, "");
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Result<Nothing> result = client.deleteSeriesByKey("key1");

    HttpRequest request = Util.captureRequest(mockClient);
    assertEquals("DELETE", request.getRequestLine().getMethod());
  }

  @Test
  public void testUri() throws IOException, URISyntaxException {
    HttpResponse response = Util.getResponse(200, "");
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Result<Nothing> result = client.deleteSeriesByKey("key1");

    HttpRequest request = Util.captureRequest(mockClient);
    URI uri = new URI(request.getRequestLine().getUri());
    assertEquals("/v1/series/key/key1/", uri.getPath());
  }
}
