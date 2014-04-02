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


public class FindDataPointsBySeriesTest {
  private static final DateTimeZone zone = DateTimeZone.UTC;
  private static final Series series = new Series("key1");

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private static final String json = "{" +
    "\"tz\":\"UTC\"," +
    "\"data\":[" +
      "{" +
        "\"interval\":{\"start\":\"2012-01-01T00:00:00.000+00:00\",\"end\":\"2012-01-02T00:00:00.000+00:00\"}," +
        "\"found\":{\"t\":\"2012-01-01T00:00:01.000+00:00\",\"v\":12.34}" +
      "}" +
    "]," +
    "\"predicate\":{" +
      "\"function\":\"max\"," +
      "\"period\":\"PT1M\"" +
    "}" +
  "}";

  private static final String json1 = "{" +
    "\"data\":[" +
      "{" +
        "\"interval\":{\"start\":\"2012-03-27T00:00:00.000+00:00\",\"end\":\"2012-03-27T00:01:00.000+00:00\"}," +
        "\"found\":{\"t\":\"2012-03-27T00:00:00.000+00:00\",\"v\":12.34}" +
      "}," +
      "{" +
        "\"interval\":{\"start\":\"2012-03-27T00:01:00.000+00:00\",\"end\":\"2012-03-27T00:02:00.000+00:00\"}," +
        "\"found\":{\"t\":\"2012-03-27T00:01:00.000+00:00\",\"v\":23.45}" +
      "}" +
    "]," +
    "\"tz\":\"UTC\"," +
    "\"predicate\":{" +
      "\"function\":\"max\"," +
      "\"period\":\"PT1M\"" +
    "}" +
  "}";

  private static final String json2 = "{" +
    "\"data\":[" +
      "{" +
        "\"interval\":{\"start\":\"2012-03-27T00:02:00.000+00:00\",\"end\":\"2012-03-27T00:03:00.000+00:00\"}," +
        "\"found\":{\"t\":\"2012-03-27T00:02:00.000+00:00\",\"v\":34.56}" +
      "}" +
    "]," +
    "\"tz\":\"UTC\"," +
    "\"predicate\":{" +
      "\"function\":\"max\"," +
      "\"period\":\"PT1M\"" +
    "}" +
  "}";

  private static final String jsonTz = "{" +
    "\"data\":[" +
      "{" +
        "\"interval\":{\"start\":\"2012-03-27T00:00:00.000+00:00\",\"end\":\"2012-03-27T00:01:00.000+00:00\"}," +
        "\"found\":{\"t\":\"2012-03-27T00:00:00.000-05:00\",\"v\":12.34}" +
      "}," +
      "{" +
        "\"interval\":{\"start\":\"2012-03-27T00:01:00.000+00:00\",\"end\":\"2012-03-27T00:02:00.000+00:00\"}," +
        "\"found\":{\"t\":\"2012-03-27T00:01:00.000-05:00\",\"v\":23.45}" +
      "}" +
    "]," +
    "\"tz\":\"America/Chicago\"," +
    "\"predicate\":{" +
      "\"function\":\"max\"," +
      "\"period\":\"PT1M\"" +
    "}" +
  "}";

  private static final DateTime start = new DateTime(2012, 3, 27, 0, 0, 0, 0, zone);
  private static final DateTime end = new DateTime(2012, 3, 28, 0, 0, 0, 0, zone);
  private static final Interval interval = new Interval(start, end);
  private static final Predicate predicate = new Predicate(Period.minutes(1), "max");
  private static final DateTime dt0 = new DateTime(2012, 3, 27, 0, 0, 0, 0, zone);
  private static final DateTime dt1 = new DateTime(2012, 3, 27, 0, 1, 0, 0, zone);
  private static final DateTime dt2 = new DateTime(2012, 3, 27, 0, 2, 0, 0, zone);
  private static final DateTime dt3 = new DateTime(2012, 3, 27, 0, 3, 0, 0, zone);

  @Test
  public void smokeTest() throws IOException {
    HttpResponse response = Util.getResponse(200, json1);
    Client client = Util.getClient(response);

    Cursor<DataPointFound> cursor = client.findDataPoints(series, new Interval(start, end), predicate, zone);

    List<DataPointFound> expected = Arrays.asList(
      new DataPointFound(new Interval(dt0, dt1), new DataPoint(dt0, 12.34)),
      new DataPointFound(new Interval(dt1, dt2), new DataPoint(dt1, 23.45))
    );

    List<DataPointFound> output = new ArrayList<DataPointFound>();
    for(DataPointFound dp : cursor) {
      output.add(dp);
    }
    assertEquals(expected, output);
  }

  @Test
  public void smokeTestTz() throws IOException {
    DateTimeZone zone = DateTimeZone.forID("America/Chicago");
    HttpResponse response = Util.getResponse(200, jsonTz);
    Client client = Util.getClient(response);

    Cursor<DataPointFound> cursor = client.findDataPoints(series, new Interval(start, end), predicate, zone);

    List<DataPointFound> expected = Arrays.asList(
      new DataPointFound(new Interval(new DateTime(2012, 3, 26, 19, 0, 0, 0, zone), new DateTime(2012, 3, 26, 19, 1, 0, 0, zone)), new DataPoint(new DateTime(2012, 3, 27, 0, 0, 0, 0, zone), 12.34)),
      new DataPointFound(new Interval(new DateTime(2012, 3, 26, 19, 1, 0, 0, zone), new DateTime(2012, 3, 26, 19, 2, 0, 0, zone)), new DataPoint(new DateTime(2012, 3, 27, 0, 1, 0, 0, zone), 23.45))
    );

    List<DataPointFound> output = new ArrayList<DataPointFound>();
    for(DataPointFound dp : cursor) {
      output.add(dp);
    }
    assertEquals(expected, output);
  }

  @Test
  public void smokeTestMultipleClientCalls() throws IOException {
    HttpResponse response1 = Util.getResponse(200, json1);
    response1.addHeader("Link", "</v1/series/key/key1/find/?start=2012-03-27T00:02:00.000-05:00&end=2012-03-28>; rel=\"next\"");
    HttpResponse response2 = Util.getResponse(200, json2);
    HttpClient mockClient = Util.getMockHttpClient(response1, response2);
    Client client = Util.getClient(mockClient);

    Cursor<DataPointFound> cursor = client.findDataPoints(series, new Interval(start, end), predicate, zone);

    List<DataPointFound> expected = Arrays.asList(
      new DataPointFound(new Interval(dt0, dt1), new DataPoint(dt0, 12.34)),
      new DataPointFound(new Interval(dt1, dt2), new DataPoint(dt1, 23.45)),
      new DataPointFound(new Interval(dt2, dt3), new DataPoint(dt2, 34.56))
    );

    List<DataPointFound> output = new ArrayList<DataPointFound>();
    for(DataPointFound dp : cursor) {
      output.add(dp);
    }
    assertEquals(expected, output);
  }

  @Test
  public void testMethod() throws IOException {
    HttpResponse response = Util.getResponse(200, json);
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Cursor<DataPointFound> cursor = client.findDataPoints(series, interval, predicate, zone);
    List<DataPointFound> output = new ArrayList<DataPointFound>();
    for(DataPointFound dp : cursor) {
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

    Cursor<DataPointFound> cursor = client.findDataPoints(series, interval, predicate, zone);
    List<DataPointFound> output = new ArrayList<DataPointFound>();
    for(DataPointFound dp : cursor) {
      output.add(dp);
    }

    HttpRequest request = Util.captureRequest(mockClient);
    URI uri = new URI(request.getRequestLine().getUri());
    assertEquals("/v1/series/key/key1/find/", uri.getPath());
  }

  @Test
  public void testParametersPredicate() throws IOException, URISyntaxException {
    HttpResponse response = Util.getResponse(200, json);
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Cursor<DataPointFound> cursor = client.findDataPoints(series, interval, predicate, zone);
    List<DataPointFound> output = new ArrayList<DataPointFound>();
    for(DataPointFound dp : cursor) {
      output.add(dp);
    }

    HttpRequest request = Util.captureRequest(mockClient);
    URI uri = new URI(request.getRequestLine().getUri());
    List<NameValuePair> params = URLEncodedUtils.parse(uri, "UTF-8");
    assertTrue(params.contains(new BasicNameValuePair("start", "2012-03-27T00:00:00.000+0000")));
    assertTrue(params.contains(new BasicNameValuePair("end", "2012-03-28T00:00:00.000+0000")));
    assertTrue(params.contains(new BasicNameValuePair("tz", "UTC")));
    assertTrue(params.contains(new BasicNameValuePair("predicate.period", "PT1M")));
    assertTrue(params.contains(new BasicNameValuePair("predicate.function", "max")));
    assertEquals(5, params.size());
  }

  @Test
  public void testError() throws IOException {
    HttpResponse response = Util.getResponse(403, "You are forbidden");
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);
    Cursor<DataPointFound> cursor = client.findDataPoints(series, interval, predicate, zone);

    thrown.expect(TempoDBException.class);
    cursor.iterator().next();
  }
}
