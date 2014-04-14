package com.tempodb;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.junit.*;
import static org.junit.Assert.*;
import org.junit.rules.ExpectedException;


public class ReadSummaryTest {
  private static final DateTimeZone zone = DateTimeZone.UTC;
  private static final Series series = new Series("key1");
  private static final Interval interval = new Interval(new DateTime(2012, 1, 1, 0, 0, 0, 0, zone), new DateTime(2012, 1, 2, 0, 0, 0, 0, zone));

  private static final Map<String, Number> data;
  static {
    data = new HashMap<String, Number>();
    data.put("max", 12.34);
    data.put("min", 23.45);
  }

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private static final String json = "{" +
    "\"summary\":{" +
      "\"max\":12.34," +
      "\"min\":23.45" +
    "}," +
    "\"tz\":\"UTC\"," +
    "\"start\":\"2012-01-01T00:00:00.000Z\"," +
    "\"end\":\"2012-01-02T00:00:00.000Z\"," +
    "\"series\":{" +
      "\"id\":\"id1\"," +
      "\"key\":\"key1\"," +
      "\"name\":\"\"," +
      "\"tags\":[]," +
      "\"attributes\":{}" +
    "}" +
  "}";

  private static final String jsonTz = "{" +
    "\"summary\":{" +
      "\"max\":12.34," +
      "\"min\":23.45" +
    "}," +
    "\"tz\":\"America/Chicago\"," +
    "\"start\":\"2012-01-01T00:00:00.000-06:00\"," +
    "\"end\":\"2012-01-02T00:00:00.000-06:00\"," +
    "\"series\":{" +
      "\"id\":\"id1\"," +
      "\"key\":\"key1\"," +
      "\"name\":\"\"," +
      "\"tags\":[]," +
      "\"attributes\":{}" +
    "}" +
  "}";

  @Test
  public void smokeTest() throws IOException {
    HttpResponse response = Util.getResponse(200, json);
    Client client = Util.getClient(response);

    Result<Summary> result = client.readSummary(series, interval);

    Result<Summary> expected = new Result<Summary>(new Summary(series, interval, data), 200, "OK");
    assertEquals(expected, result);
  }

  @Test
  public void smokeTestTz() throws IOException {
    DateTimeZone zone = DateTimeZone.forID("America/Chicago");
    HttpResponse response = Util.getResponse(200, jsonTz);
    Client client = Util.getClient(response);
    Interval interval = new Interval(new DateTime(2012, 1, 1, 0, 0, 0, 0, zone), new DateTime(2012, 1, 2, 0, 0, 0, 0, zone));

    Result<Summary> result = client.readSummary(series, interval);

    Result<Summary> expected = new Result<Summary>(new Summary(series, interval, data), 200, "OK");
    assertEquals(expected, result);
  }

  @Test
  public void testMethod() throws IOException {
    HttpResponse response = Util.getResponse(200, json);
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Result<Summary> result = client.readSummary(series, interval);

    HttpRequest request = Util.captureRequest(mockClient);
    assertEquals("GET", request.getRequestLine().getMethod());
  }

  @Test
  public void testUri() throws IOException, URISyntaxException {
    HttpResponse response = Util.getResponse(200, json);
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Result<Summary> result = client.readSummary(series, interval);

    HttpRequest request = Util.captureRequest(mockClient);
    URI uri = new URI(request.getRequestLine().getUri());
    assertEquals("/v1/series/key/key1/summary/", uri.getPath());
  }

  @Test
  public void testParameters() throws IOException, URISyntaxException {
    HttpResponse response = Util.getResponse(200, json);
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Result<Summary> result = client.readSummary(series, interval);

    HttpRequest request = Util.captureRequest(mockClient);
    URI uri = new URI(request.getRequestLine().getUri());
    List<NameValuePair> params = URLEncodedUtils.parse(uri, "UTF-8");
    assertTrue(params.contains(new BasicNameValuePair("start", "2012-01-01T00:00:00.000+0000")));
    assertTrue(params.contains(new BasicNameValuePair("end", "2012-01-02T00:00:00.000+0000")));
    assertTrue(params.contains(new BasicNameValuePair("tz", "UTC")));
    assertEquals(3, params.size());
  }
}
