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
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;


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

  private static final Series series1 = new Series("key1", "name1", new HashSet(), new HashMap());
  private static final Series series2 = new Series("key2", "name2", new HashSet(), new HashMap());
  private static final Series series3 = new Series("key3", "name3", new HashSet(), new HashMap());
  private static final Filter filter = new Filter();

  @Test
  public void smokeTest() throws IOException {
    HttpResponse response = Util.getResponse(200, json);
    Client client = Util.getClient(response);

    List<Series> expected = Arrays.asList(series1);

    Cursor<Series> cursor = client.getSeriesByFilter(filter);
    List<Series> output = new ArrayList();
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
    List<Series> output = new ArrayList();
    for(Series series : cursor) {
      output.add(series);
    }

    ArgumentCaptor<HttpRequest> argument = ArgumentCaptor.forClass(HttpRequest.class);
    verify(mockClient).execute(any(HttpHost.class), argument.capture());
    assertEquals("GET", argument.getValue().getRequestLine().getMethod());
  }

  @Test
  public void testUri() throws IOException, URISyntaxException {
    HttpResponse response = Util.getResponse(200, json);
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Cursor<Series> cursor = client.getSeriesByFilter(filter);
    List<Series> output = new ArrayList();
    for(Series series : cursor) {
      output.add(series);
    }

    ArgumentCaptor<HttpRequest> argument = ArgumentCaptor.forClass(HttpRequest.class);
    verify(mockClient).execute(any(HttpHost.class), argument.capture());

    URI uri = new URI(argument.getValue().getRequestLine().getUri());
    assertEquals("/v1/series/", argument.getValue().getRequestLine().getUri());
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
    List<Series> output = new ArrayList();
    for(Series series : cursor) {
      output.add(series);
    }

    ArgumentCaptor<HttpRequest> argument = ArgumentCaptor.forClass(HttpRequest.class);
    verify(mockClient).execute(any(HttpHost.class), argument.capture());

    URI uri = new URI(argument.getValue().getRequestLine().getUri());
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
