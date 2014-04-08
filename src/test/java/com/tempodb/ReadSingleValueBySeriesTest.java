package com.tempodb;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.junit.*;
import static org.junit.Assert.*;
import org.junit.rules.ExpectedException;


public class ReadSingleValueBySeriesTest {
  private static final DateTimeZone zone = DateTimeZone.UTC;
  private static final Series series = new Series("key1");
  private static final DateTime timestamp = new DateTime(2012, 1, 1, 0, 0, 1, 0, zone);

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private static final String json = "{" +
      "\"series\":{" +
        "\"id\":\"id1\"," +
        "\"key\":\"key1\"," +
        "\"name\":\"\"," +
        "\"tags\":[]," +
        "\"attributes\":{}" +
      "}," +
      "\"data\":{" +
        "\"t\":\"2012-01-01T00:00:01.000Z\"," +
        "\"v\":12.34" +
      "}," +
      "\"tz\":\"UTC\"" +
    "}";

  private static final String jsonTz = "{" +
      "\"series\":{" +
        "\"id\":\"id1\"," +
        "\"key\":\"key1\"," +
        "\"name\":\"\"," +
        "\"tags\":[]," +
        "\"attributes\":{}" +
      "}," +
      "\"data\":{" +
        "\"t\":\"2012-01-01T00:00:01.000-06:00\"," +
        "\"v\":12.34" +
      "}," +
      "\"tz\":\"America/Chicago\"" +
    "}";

  @Test
  public void smokeTest() throws IOException {
    HttpResponse response = Util.getResponse(200, json);
    Client client = Util.getClient(response);
    DateTime timestamp = new DateTime(2012, 1, 1, 0, 0, 1, 0, zone);

    Result<SingleValue> result = client.readSingleValue(series, timestamp, zone);

    DataPoint datapoint = new DataPoint(timestamp, 12.34);
    Result<SingleValue> expected = new Result<SingleValue>(new SingleValue(series, datapoint), 200, "OK");
    assertEquals(expected, result);
  }

  @Test
  public void smokeTestTz() throws IOException {
    DateTimeZone zone = DateTimeZone.forID("America/Chicago");
    HttpResponse response = Util.getResponse(200, jsonTz);
    Client client = Util.getClient(response);
    DateTime timestamp = new DateTime(2012, 1, 1, 0, 0, 1, 0, zone);

    Result<SingleValue> result = client.readSingleValue(series, timestamp, zone);

    DataPoint datapoint = new DataPoint(timestamp, 12.34);
    Result<SingleValue> expected = new Result<SingleValue>(new SingleValue(series, datapoint), 200, "OK");
    assertEquals(expected, result);
  }

  @Test
  public void testMethod() throws IOException {
    HttpResponse response = Util.getResponse(200, json);
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Result<SingleValue> result = client.readSingleValue(series, timestamp, zone);

    HttpRequest request = Util.captureRequest(mockClient);
    assertEquals("GET", request.getRequestLine().getMethod());
  }

  @Test
  public void testUri() throws IOException, URISyntaxException {
    HttpResponse response = Util.getResponse(200, json);
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Result<SingleValue> result = client.readSingleValue(series, timestamp, zone);

    HttpRequest request = Util.captureRequest(mockClient);
    URI uri = new URI(request.getRequestLine().getUri());
    assertEquals("/v1/series/key/key1/single/", uri.getPath());
  }

  @Test
  public void testParameters() throws IOException, URISyntaxException {
    HttpResponse response = Util.getResponse(200, json);
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Result<SingleValue> result = client.readSingleValue(series, timestamp, zone, Direction.NEAREST);

    HttpRequest request = Util.captureRequest(mockClient);
    URI uri = new URI(request.getRequestLine().getUri());
    List<NameValuePair> params = URLEncodedUtils.parse(uri, "UTF-8");
    assertTrue(params.contains(new BasicNameValuePair("ts", "2012-01-01T00:00:01.000+0000")));
    assertTrue(params.contains(new BasicNameValuePair("tz", "UTC")));
    assertTrue(params.contains(new BasicNameValuePair("direction", "nearest")));
    assertEquals(3, params.size());
  }
}
