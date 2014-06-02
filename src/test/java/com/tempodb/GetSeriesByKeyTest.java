package com.tempodb;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.junit.*;
import static org.junit.Assert.*;


public class GetSeriesByKeyTest {

  private static final String json  = "{\"id\":\"id1\",\"key\":\"key1\",\"name\":\"name1\",\"tags\":[],\"attributes\":{}}";
  private static final String json2 = "{\"id\":\"id1\",\"key\":\"appidÜ:1234.txn:/def ault.count\",\"name\":\"name1\",\"tags\":[],\"attributes\":{\"appidÜ\":\"1234\",\"txn\":\"def ault\"}}";

  @Test
  public void smokeTest() throws IOException {
    HttpResponse response = Util.getResponse(200, json);
    Client client = Util.getClient(response);

    Result<Series> expected = new Result<Series>(new Series("key1", "name1", new HashSet<String>(), new HashMap<String, String>()), 200, "OK");
    Result<Series> result = client.getSeries("key1");
    assertEquals(expected, result);
  }

  @Test
  public void smokeTestUnescapedKey() throws IOException, URISyntaxException {
    HttpResponse response = Util.getResponse(200, json2);
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Map<String, String> attributes = new HashMap<String, String>();
    attributes.put("appidÜ", "1234");
    attributes.put("txn", "def ault");
    Result<Series> expected = new Result<Series>(new Series("appidÜ:1234.txn:/def ault.count", "name1", new HashSet<String>(), attributes), 200, "OK");

    Result<Series> result = client.getSeries("appidÜ:1234.txn:/def ault.count");
    assertEquals(expected, result);

    HttpRequest request = Util.captureRequest(mockClient);
    URI uri = new URI(request.getRequestLine().getUri());
    assertEquals("/v1/series/key/appid%C3%9C%3A1234.txn%3A%2Fdef%20ault.count/", uri.getRawPath());
  }

  @Test
  public void testMethod() throws IOException {
    HttpResponse response = Util.getResponse(200, json);
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Result<Series> result = client.getSeries("key1");

    HttpRequest request = Util.captureRequest(mockClient);
    assertEquals("GET", request.getRequestLine().getMethod());
  }

  @Test
  public void testUri() throws IOException, URISyntaxException {
    HttpResponse response = Util.getResponse(200, json);
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Result<Series> result = client.getSeries("key1");

    HttpRequest request = Util.captureRequest(mockClient);
    URI uri = new URI(request.getRequestLine().getUri());
    assertEquals("/v1/series/key/key1/", uri.getPath());
  }
}
