package com.tempodb.unit;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.junit.*;
import static org.junit.Assert.*;

import com.tempodb.Client;
import com.tempodb.Nothing;
import com.tempodb.Result;
import com.tempodb.Util;


public class DeleteDataPointsByKeyTest {
  private DateTimeZone timezone = DateTimeZone.UTC;
  private DateTime start = new DateTime(2012, 1, 1, 0, 0, 0, 0, timezone);
  private DateTime end = new DateTime(2012, 1, 2, 0, 0, 0, 0, timezone);

  @Test
  public void smokeTest() throws IOException {
    HttpResponse response = Util.getResponse(200, "");
    Client client = Util.getClient(response);

    Result<Nothing> result = client.deleteDataPointsByKey("key1", new Interval(start, end));
    Result<Nothing> expected = new Result<Nothing>(new Nothing(), 200, "OK");
    assertEquals(expected, result);
  }

  @Test
  public void testMethod() throws IOException {
    HttpResponse response = Util.getResponse(200, "");
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Result<Nothing> result = client.deleteDataPointsByKey("key1", new Interval(start, end));

    HttpRequest request = Util.captureRequest(mockClient);
    assertEquals("DELETE", request.getRequestLine().getMethod());
  }

  @Test
  public void testUri() throws IOException, URISyntaxException {
    HttpResponse response = Util.getResponse(200, "");
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Result<Nothing> result = client.deleteDataPointsByKey("key1", new Interval(start, end));

    HttpRequest request = Util.captureRequest(mockClient);
    URI uri = new URI(request.getRequestLine().getUri());
    assertEquals("/v1/series/key/key1/data/", uri.getPath());
  }

  @Test
  public void testParameters() throws IOException, URISyntaxException {
    HttpResponse response = Util.getResponse(200, "");
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Result<Nothing> result = client.deleteDataPointsByKey("key1", new Interval(start, end));

    HttpRequest request = Util.captureRequest(mockClient);
    URI uri = new URI(request.getRequestLine().getUri());
    List<NameValuePair> params = URLEncodedUtils.parse(uri, "UTF-8");
    assertTrue(params.contains(new BasicNameValuePair("start", "2012-01-01T00:00:00.000+0000")));
    assertTrue(params.contains(new BasicNameValuePair("end", "2012-01-02T00:00:00.000+0000")));
    assertEquals(2, params.size());
  }
}
