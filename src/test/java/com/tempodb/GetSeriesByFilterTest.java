package com.tempodb;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.junit.*;
import static org.junit.Assert.*;
import org.junit.rules.ExpectedException;


public class GetSeriesByFilterTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private static final String json = "[" +
    "{\"id\":\"id1\",\"key\":\"key1\",\"name\":\"name1\",\"tags\":[],\"attributes\":{}}" +
  "]";

  private static final String json1 = "[" +
    "{\"id\":\"id1\",\"key\":\"key1\",\"name\":\"name1\",\"tags\":[],\"attributes\":{}}," +
    "{\"id\":\"id2\",\"key\":\"key2\",\"name\":\"name2\",\"tags\":[],\"attributes\":{}}" +
  "]";

  private static final String json2 = "[" +
    "{\"id\":\"id3\",\"key\":\"key3\",\"name\":\"name3\",\"tags\":[],\"attributes\":{}}" +
  "]";

  private static final Series series1 = new Series("key1", "name1", new HashSet<String>(), new HashMap<String, String>());
  private static final Series series2 = new Series("key2", "name2", new HashSet<String>(), new HashMap<String, String>());
  private static final Series series3 = new Series("key3", "name3", new HashSet<String>(), new HashMap<String, String>());
  private static final Filter filter = new Filter();

  @Test
  public void smokeTest() throws IOException {
    HttpResponse response = Util.getResponse(200, json);
    Client client = Util.getClient(response);

    List<Series> expected = Arrays.asList(series1);

    Cursor<Series> cursor = client.getSeriesByFilter(filter);
    List<Series> output = new ArrayList<Series>();
    for(Series series : cursor) {
      output.add(series);
    }
    assertEquals(expected, output);
  }

  @Test
  public void smokeTestMultipleClientCalls() throws IOException {
    HttpResponse response1 = Util.getResponse(200, json1);
    response1.addHeader("Link", "</v1/series/>; rel=\"next\"");
    HttpResponse response2 = Util.getResponse(200, json2);
    HttpClient mockClient = Util.getMockHttpClient(response1, response2);
    Client client = Util.getClient(mockClient);

    List<Series> expected = Arrays.asList(series1, series2, series3);

    Cursor<Series> cursor = client.getSeriesByFilter(filter);
    List<Series> output = new ArrayList<Series>();
    for(Series series : cursor) {
      output.add(series);
    }
    assertEquals(expected, output);
  }

  @Test
  public void testMethod() throws IOException {
    HttpResponse response = Util.getResponse(200, json);
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Cursor<Series> cursor = client.getSeriesByFilter(filter);
    List<Series> output = new ArrayList<Series>();
    for(Series series : cursor) {
      output.add(series);
    }

    HttpRequest request = Util.captureRequest(mockClient);
    assertEquals("GET", request.getRequestLine().getMethod());
  }

  @Test
  public void testUri() throws IOException, URISyntaxException {
    HttpResponse response = Util.getResponse(200, json);
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Cursor<Series> cursor = client.getSeriesByFilter(filter);
    List<Series> output = new ArrayList<Series>();
    for(Series series : cursor) {
      output.add(series);
    }

    HttpRequest request = Util.captureRequest(mockClient);
    URI uri = new URI(request.getRequestLine().getUri());
    assertEquals("/v1/series/", uri.getPath());
  }

  @Test
  public void testParameters() throws IOException, URISyntaxException {
    HttpResponse response = Util.getResponse(200, json);
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Filter filter = new Filter();
    filter.addKey("key1");
    filter.addTag("tag1");
    filter.addTag("tag1");
    filter.addAttribute("key1", "value1");
    filter.addAttribute("key2", "value2");

    Cursor<Series> cursor = client.getSeriesByFilter(filter);
    List<Series> output = new ArrayList<Series>();
    for(Series series : cursor) {
      output.add(series);
    }

    HttpRequest request = Util.captureRequest(mockClient);
    URI uri = new URI(request.getRequestLine().getUri());
    List<NameValuePair> params = URLEncodedUtils.parse(uri, "UTF-8");
    assertTrue(params.contains(new BasicNameValuePair("key", "key1")));
    assertTrue(params.contains(new BasicNameValuePair("tag", "tag1")));
    assertTrue(params.contains(new BasicNameValuePair("attr[key1]", "value1")));
    assertTrue(params.contains(new BasicNameValuePair("attr[key2]", "value2")));
    assertEquals(4, params.size());
  }

  @Test
  public void testError() throws IOException {
    HttpResponse response = Util.getResponse(403, "You are forbidden");
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Cursor<Series> cursor = client.getSeriesByFilter(filter);

    thrown.expect(TempoDBException.class);
    cursor.iterator().next();
  }
}
