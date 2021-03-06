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


public class ReadDataPointsBySeriesTest {
  private static final DateTimeZone zone = DateTimeZone.UTC;
  private static final Series series = new Series("key1");

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private static final String json = "{" +
    "\"rollup\":{" +
      "\"fold\":\"sum\"," +
      "\"period\":\"PT1H\"" +
    "}," +
    "\"tz\":\"UTC\"," +
    "\"data\":[" +
      "{\"t\":\"2012-01-01T00:00:00.000Z\",\"v\":12.34}" +
    "]" +
  "}";

  private static final String json1 = "{" +
    "\"rollup\":null," +
    "\"tz\":\"UTC\"," +
    "\"data\":[" +
      "{\"t\":\"2012-03-27T05:00:00.000Z\",\"v\":12.34}," +
      "{\"t\":\"2012-03-27T05:01:00.000Z\",\"v\":23.45}" +
    "]" +
  "}";

  private static final String json2 = "{" +
    "\"rollup\":null," +
    "\"tz\":\"UTC\"," +
    "\"data\":[" +
      "{\"t\":\"2012-03-27T05:02:00.000Z\",\"v\":34.56}" +
    "]" +
  "}";

  private static final String jsonTz = "{" +
    "\"rollup\":null," +
    "\"tz\":\"America/Chicago\"," +
    "\"data\":[" +
      "{\"t\":\"2012-01-01T00:00:00.000-06:00\",\"v\":34.56}" +
    "]" +
  "}";

  private static final DateTime start = new DateTime(2012, 1, 1, 0, 0, 0, 0, zone);
  private static final DateTime end = new DateTime(2012, 1, 2, 0, 0, 0, 0, zone);
  private static final Interval interval = new Interval(start, end);
  private static final Rollup rollup = new Rollup(Period.minutes(1), Fold.SUM);

  @Test
  public void smokeTest() throws IOException {
    HttpResponse response = Util.getResponse(200, json1);
    Client client = Util.getClient(response);
    DateTime start = new DateTime(2012, 3, 27, 0, 0, 0, zone);
    DateTime end = new DateTime(2012, 3, 28, 0, 0, 0, zone);

    List<DataPoint> expected = Arrays.asList(new DataPoint(new DateTime(2012, 3, 27, 5, 0, 0, 0, zone), 12.34),
                                             new DataPoint(new DateTime(2012, 3, 27, 5, 1, 0, 0, zone), 23.45));

    Cursor<DataPoint> cursor = client.readDataPoints(series, new Interval(start, end), zone);
    List<DataPoint> output = new ArrayList<DataPoint>();
    for(DataPoint dp : cursor) {
      output.add(dp);
    }
    assertEquals(expected, output);
  }

  @Test
  public void smokeTestNext() throws IOException {
    HttpResponse response = Util.getResponse(200, json1);
    Client client = Util.getClient(response);
    DateTime start = new DateTime(2012, 3, 27, 0, 0, 0, zone);
    DateTime end = new DateTime(2012, 3, 28, 0, 0, 0, zone);

    DataPoint expected = new DataPoint(new DateTime(2012, 3, 27, 5, 0, 0, 0, zone), 12.34);

    Cursor<DataPoint> cursor = client.readDataPoints(series, new Interval(start, end), zone);
    assertEquals(expected, cursor.iterator().next());
  }

  @Test
  public void smokeTestTz() throws IOException {
    DateTimeZone zone = DateTimeZone.forID("America/Chicago");
    HttpResponse response = Util.getResponse(200, jsonTz);
    Client client = Util.getClient(response);
    DateTime start = new DateTime(2012, 1, 1, 0, 0, 0, zone);
    DateTime end = new DateTime(2012, 1, 1, 0, 0, 0, zone);

    List<DataPoint> expected = Arrays.asList(new DataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0, zone), 34.56));

    Cursor<DataPoint> cursor = client.readDataPoints(series, new Interval(start, end), zone);
    List<DataPoint> output = new ArrayList<DataPoint>();
    for(DataPoint dp : cursor) {
      output.add(dp);
    }
    assertEquals(expected, output);
  }

  @Test
  public void smokeTestMultipleClientCalls() throws IOException {
    HttpResponse response1 = Util.getResponse(200, json1);
    response1.addHeader("Link", "</v1/series/key/key1/segment/?start=2012-03-27T00:02:00.000-05:00&end=2012-03-28>; rel=\"next\"");
    HttpResponse response2 = Util.getResponse(200, json2);
    HttpClient mockClient = Util.getMockHttpClient(response1, response2);
    Client client = Util.getClient(mockClient);
    DateTime start = new DateTime(2012, 3, 27, 0, 0, 0, 0, zone);
    DateTime end = new DateTime(2012, 3, 28, 0, 0, 0, 0, zone);

    List<DataPoint> expected = Arrays.asList(new DataPoint(new DateTime(2012, 3, 27, 5, 0, 0, 0, zone), 12.34),
                                             new DataPoint(new DateTime(2012, 3, 27, 5, 1, 0, 0, zone), 23.45),
                                             new DataPoint(new DateTime(2012, 3, 27, 5, 2, 0, 0, zone), 34.56));

    Cursor<DataPoint> cursor = client.readDataPoints(series, new Interval(start, end), zone);
    List<DataPoint> output = new ArrayList<DataPoint>();
    for(DataPoint dp : cursor) {
      output.add(dp);
    }
    assertEquals(expected, output);
  }

  @Test
  public void testMethod() throws IOException {
    HttpResponse response = Util.getResponse(200, json);
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Cursor<DataPoint> cursor = client.readDataPoints(series, interval, zone);
    List<DataPoint> output = new ArrayList<DataPoint>();
    for(DataPoint dp : cursor) {
      output.add(dp);
    }

    HttpRequest request = Util.captureRequest(mockClient);
    assertEquals("GET", request.getRequestLine().getMethod());
  }

  @Test
  public void testUri() throws IOException, URISyntaxException {
    HttpResponse response = Util.getResponse(200, json);
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Cursor<DataPoint> cursor = client.readDataPoints(series, interval, zone);
    List<DataPoint> output = new ArrayList<DataPoint>();
    for(DataPoint dp : cursor) {
      output.add(dp);
    }

    HttpRequest request = Util.captureRequest(mockClient);
    URI uri = new URI(request.getRequestLine().getUri());
    assertEquals("/v1/series/key/key1/segment/", uri.getPath());
  }

  @Test
  public void testParametersNoRollup() throws IOException, URISyntaxException {
    HttpResponse response = Util.getResponse(200, json);
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Cursor<DataPoint> cursor = client.readDataPoints(series, interval, zone);
    List<DataPoint> output = new ArrayList<DataPoint>();
    for(DataPoint dp : cursor) {
      output.add(dp);
    }

    HttpRequest request = Util.captureRequest(mockClient);
    URI uri = new URI(request.getRequestLine().getUri());
    List<NameValuePair> params = URLEncodedUtils.parse(uri, "UTF-8");
    assertTrue(params.contains(new BasicNameValuePair("start", "2012-01-01T00:00:00.000+0000")));
    assertTrue(params.contains(new BasicNameValuePair("end", "2012-01-02T00:00:00.000+0000")));
    assertTrue(params.contains(new BasicNameValuePair("tz", "UTC")));
    assertEquals(3, params.size());
  }

  @Test
  public void testParametersRollup() throws IOException, URISyntaxException {
    HttpResponse response = Util.getResponse(200, json);
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Cursor<DataPoint> cursor = client.readDataPoints(series, interval, zone, rollup, null);
    List<DataPoint> output = new ArrayList<DataPoint>();
    for(DataPoint dp : cursor) {
      output.add(dp);
    }

    HttpRequest request = Util.captureRequest(mockClient);
    URI uri = new URI(request.getRequestLine().getUri());
    List<NameValuePair> params = URLEncodedUtils.parse(uri, "UTF-8");
    assertTrue(params.contains(new BasicNameValuePair("start", "2012-01-01T00:00:00.000+0000")));
    assertTrue(params.contains(new BasicNameValuePair("end", "2012-01-02T00:00:00.000+0000")));
    assertTrue(params.contains(new BasicNameValuePair("tz", "UTC")));
    assertTrue(params.contains(new BasicNameValuePair("rollup.period", "PT1M")));
    assertTrue(params.contains(new BasicNameValuePair("rollup.fold", "sum")));
    assertEquals(5, params.size());
  }

  @Test
  public void testParametersRollupInterpolation() throws IOException, URISyntaxException {
    HttpResponse response = Util.getResponse(200, json);
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Interpolation interpolation = Interpolation.zoh(Period.minutes(1));
    Cursor<DataPoint> cursor = client.readDataPoints(series, interval, zone, rollup, interpolation);
    List<DataPoint> output = new ArrayList<DataPoint>();
    for(DataPoint dp : cursor) {
      output.add(dp);
    }

    HttpRequest request = Util.captureRequest(mockClient);
    URI uri = new URI(request.getRequestLine().getUri());
    List<NameValuePair> params = URLEncodedUtils.parse(uri, "UTF-8");
    assertTrue(params.contains(new BasicNameValuePair("start", "2012-01-01T00:00:00.000+0000")));
    assertTrue(params.contains(new BasicNameValuePair("end", "2012-01-02T00:00:00.000+0000")));
    assertTrue(params.contains(new BasicNameValuePair("tz", "UTC")));
    assertTrue(params.contains(new BasicNameValuePair("rollup.period", "PT1M")));
    assertTrue(params.contains(new BasicNameValuePair("rollup.fold", "sum")));
    assertTrue(params.contains(new BasicNameValuePair("interpolation.period", "PT1M")));
    assertTrue(params.contains(new BasicNameValuePair("interpolation.function", "zoh")));
    assertEquals(7, params.size());
  }

  @Test
  public void testError() throws IOException {
    HttpResponse response = Util.getResponse(403, "You are forbidden");
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);
    Cursor<DataPoint> cursor = client.readDataPoints(series, interval, zone, rollup, null);

    thrown.expect(TempoDBException.class);
    cursor.iterator().next();
  }
}
