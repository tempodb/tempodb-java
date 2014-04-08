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
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.*;
import static org.junit.Assert.*;
import org.junit.rules.ExpectedException;


public class ReadSingleValueByFilterTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private static final String json = "[{" +
    "\"series\":{" +
      "\"id\":\"id1\"," +
      "\"key\":\"key1\"," +
      "\"name\":\"name1\"," +
      "\"tags\":[]," +
      "\"attributes\":{}" +
    "}," +
    "\"data\":{" +
      "\"t\":\"2012-01-01T00:00:01.000Z\"," +
      "\"v\":12.34" +
    "}," +
    "\"tz\":\"UTC\"" +
  "}]";

  private static final String json1 = "[" +
    "{" +
      "\"series\":{" +
        "\"id\":\"id1\"," +
        "\"key\":\"key1\"," +
        "\"name\":\"name1\"," +
        "\"tags\":[]," +
        "\"attributes\":{}" +
      "}," +
      "\"data\":{" +
        "\"t\":\"2012-01-01T00:00:01.000Z\"," +
        "\"v\":12.34" +
      "}," +
      "\"tz\":\"UTC\"" +
    "}," +
    "{" +
      "\"series\":{" +
        "\"id\":\"id2\"," +
        "\"key\":\"key2\"," +
        "\"name\":\"name2\"," +
        "\"tags\":[]," +
        "\"attributes\":{}" +
      "}," +
      "\"data\":{" +
        "\"t\":\"2012-01-01T00:00:01.000Z\"," +
        "\"v\":23.45" +
      "}," +
      "\"tz\":\"UTC\"" +
    "}" +
  "]";

  private static final String json2 = "[" +
    "{" +
      "\"series\":{" +
        "\"id\":\"id3\"," +
        "\"key\":\"key3\"," +
        "\"name\":\"name3\"," +
        "\"tags\":[]," +
        "\"attributes\":{}" +
      "}," +
      "\"data\":{" +
        "\"t\":\"2012-01-01T00:00:01.000Z\"," +
        "\"v\":34.56" +
      "}," +
      "\"tz\":\"UTC\"" +
    "}" +
  "]";

  private static final String jsonTz = "[{" +
    "\"series\":{" +
      "\"id\":\"id1\"," +
      "\"key\":\"key1\"," +
      "\"name\":\"name1\"," +
      "\"tags\":[]," +
      "\"attributes\":{}" +
    "}," +
    "\"data\":{" +
      "\"t\":\"2012-01-01T00:00:01.000-06:00\"," +
      "\"v\":12.34" +
    "}," +
    "\"tz\":\"America/Chicago\"" +
  "}]";

  private static final DateTimeZone zone = DateTimeZone.UTC;
  private static final Series series1 = new Series("key1", "name1", new HashSet<String>(), new HashMap<String, String>());
  private static final Series series2 = new Series("key2", "name2", new HashSet<String>(), new HashMap<String, String>());
  private static final Series series3 = new Series("key3", "name3", new HashSet<String>(), new HashMap<String, String>());
  private static final DateTime timestamp = new DateTime(2012, 1, 1, 0, 0, 1, 0, zone);
  private static final Filter filter = new Filter();

  @Test
  public void smokeTest() throws IOException {
    HttpResponse response = Util.getResponse(200, json);
    Client client = Util.getClient(response);

    List<SingleValue> expected = Arrays.asList(new SingleValue(series1, new DataPoint(timestamp, 12.34)));

    Cursor<SingleValue> cursor = client.readSingleValue(filter, timestamp, zone, Direction.EXACT);
    List<SingleValue> output = new ArrayList<SingleValue>();
    for(SingleValue value : cursor) {
      output.add(value);
    }
    assertEquals(expected, output);
  }

  @Test
  public void smokeTestTz() throws IOException {
    DateTimeZone zone = DateTimeZone.forID("America/Chicago");
    HttpResponse response = Util.getResponse(200, jsonTz);
    Client client = Util.getClient(response);

    List<SingleValue> expected = Arrays.asList(new SingleValue(series1, new DataPoint(new DateTime(2012, 1, 1, 0, 0, 1, 0, zone), 12.34)));

    Cursor<SingleValue> cursor = client.readSingleValue(filter, timestamp, zone, Direction.EXACT);
    List<SingleValue> output = new ArrayList<SingleValue>();
    for(SingleValue value : cursor) {
      output.add(value);
    }
    assertEquals(expected, output);
  }

  @Test
  public void smokeTestMultipleClientCalls() throws IOException {
    HttpResponse response1 = Util.getResponse(200, json1);
    response1.addHeader("Link", "</v1/single/>; rel=\"next\"");
    HttpResponse response2 = Util.getResponse(200, json2);
    HttpClient mockClient = Util.getMockHttpClient(response1, response2);
    Client client = Util.getClient(mockClient);

    List<SingleValue> expected = Arrays.asList(
      new SingleValue(series1, new DataPoint(timestamp, 12.34)),
      new SingleValue(series2, new DataPoint(timestamp, 23.45)),
      new SingleValue(series3, new DataPoint(timestamp, 34.56))
    );

    Cursor<SingleValue> cursor = client.readSingleValue(filter, timestamp, zone, Direction.EXACT);
    List<SingleValue> output = new ArrayList<SingleValue>();
    for(SingleValue value : cursor) {
      output.add(value);
    }
    assertEquals(expected, output);
  }

  @Test
  public void testMethod() throws IOException {
    HttpResponse response = Util.getResponse(200, json);
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Cursor<SingleValue> cursor = client.readSingleValue(filter, timestamp, zone, Direction.EXACT);
    List<SingleValue> output = new ArrayList<SingleValue>();
    for(SingleValue value : cursor) {
      output.add(value);
    }

    HttpRequest request = Util.captureRequest(mockClient);
    assertEquals("GET", request.getRequestLine().getMethod());
  }

  @Test
  public void testUri() throws IOException, URISyntaxException {
    HttpResponse response = Util.getResponse(200, json);
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Cursor<SingleValue> cursor = client.readSingleValue(filter, timestamp, zone, Direction.EXACT);
    List<SingleValue> output = new ArrayList<SingleValue>();
    for(SingleValue value : cursor) {
      output.add(value);
    }

    HttpRequest request = Util.captureRequest(mockClient);
    URI uri = new URI(request.getRequestLine().getUri());
    assertEquals("/v1/single/", uri.getPath());
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

    Cursor<SingleValue> cursor = client.readSingleValue(filter, timestamp, zone, Direction.EXACT);
    List<SingleValue> output = new ArrayList<SingleValue>();
    for(SingleValue value : cursor) {
      output.add(value);
    }

    HttpRequest request = Util.captureRequest(mockClient);
    URI uri = new URI(request.getRequestLine().getUri());
    List<NameValuePair> params = URLEncodedUtils.parse(uri, "UTF-8");
    assertTrue(params.contains(new BasicNameValuePair("ts", "2012-01-01T00:00:01.000+0000")));
    assertTrue(params.contains(new BasicNameValuePair("key", "key1")));
    assertTrue(params.contains(new BasicNameValuePair("tag", "tag1")));
    assertTrue(params.contains(new BasicNameValuePair("attr[key1]", "value1")));
    assertTrue(params.contains(new BasicNameValuePair("attr[key2]", "value2")));
    assertTrue(params.contains(new BasicNameValuePair("tz", "UTC")));
    assertTrue(params.contains(new BasicNameValuePair("direction", "exact")));
    assertEquals(7, params.size());
  }

  @Test
  public void testError() throws IOException {
    HttpResponse response = Util.getResponse(403, "You are forbidden");
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Cursor<SingleValue> cursor = client.readSingleValue(filter, timestamp, zone, Direction.EXACT);

    thrown.expect(TempoDBException.class);
    cursor.iterator().next();
  }
}
