package com.tempodb.unit;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.junit.*;
import static org.junit.Assert.*;

import com.tempodb.Client;
import com.tempodb.Result;
import com.tempodb.Series;


public class GetSeriesByKeyTest {

  private static final String json = "{\"id\":\"id1\",\"key\":\"key1\",\"name\":\"name1\",\"tags\":[],\"attributes\":{}}";

  @Test
  public void smokeTest() throws IOException {
    HttpResponse response = Util.getResponse(200, json);
    Client client = Util.getClient(response);

    Result<Series> expected = new Result<Series>(new Series("key1", "name1", new HashSet<String>(), new HashMap<String, String>()), 200, "OK");
    Result<Series> result = client.getSeriesByKey("key1");
    assertEquals(expected, result);
  }

  @Test
  public void testMethod() throws IOException {
    HttpResponse response = Util.getResponse(200, json);
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Result<Series> result = client.getSeriesByKey("key1");

    HttpRequest request = Util.captureRequest(mockClient);
    assertEquals("GET", request.getRequestLine().getMethod());
  }

  @Test
  public void testUri() throws IOException, URISyntaxException {
    HttpResponse response = Util.getResponse(200, json);
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Result<Series> result = client.getSeriesByKey("key1");

    HttpRequest request = Util.captureRequest(mockClient);
    URI uri = new URI(request.getRequestLine().getUri());
    assertEquals("/v1/series/key/key1/", uri.getPath());
  }
}
