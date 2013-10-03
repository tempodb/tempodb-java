package com.tempodb.unit;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;

import com.tempodb.Client;
import com.tempodb.DataPoint;
import com.tempodb.MultiStatus;
import com.tempodb.Nothing;
import com.tempodb.Result;
import com.tempodb.Series;
import com.tempodb.Status;
import com.tempodb.Util;
import com.tempodb.WriteRequest;


public class WriteDataPointsTest {

  private static final String json = "[" +
    "{\"key\":\"key1\",\"t\":\"2012-03-27T05:00:00.000Z\",\"v\":12.34}," +
    "{\"key\":\"key2\",\"t\":\"2012-03-27T05:01:00.000Z\",\"v\":23.45}" +
  "]";

  private static final String multistatus_json = "{\"multistatus\":[{\"status\":403,\"messages\":[\"Forbidden\"]}]}";

  private static final DateTimeZone timezone = DateTimeZone.UTC;
  private static final WriteRequest wr = new WriteRequest()
    .add(new Series("key1"), new DataPoint(new DateTime(2012, 3, 27, 5, 0, 0, 0, timezone), 12.34))
    .add(new Series("key2"), new DataPoint(new DateTime(2012, 3, 27, 5, 1, 0, 0, timezone), 23.45));

  private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

  @Test
  public void smokeTest() throws IOException {
    HttpResponse response = Util.getResponse(200, "");
    Client client = Util.getClient(response);
    Result<Nothing> result = client.writeDataPoints(wr);

    Result<Nothing> expected = new Result<Nothing>(new Nothing(), 200, "OK");
    assertEquals(expected, result);
    assertEquals("OK", result.getMessage());
  }

  @Test
  public void testMethod() throws IOException {
    HttpResponse response = Util.getResponse(200, "");
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Result<Nothing> result = client.writeDataPoints(wr);

    HttpRequest request = Util.captureRequest(mockClient);
    assertEquals("POST", request.getRequestLine().getMethod());
  }

  @Test
  public void testUri() throws IOException, URISyntaxException {
    HttpResponse response = Util.getResponse(200, "");
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Result<Nothing> result = client.writeDataPoints(wr);

    HttpRequest request = Util.captureRequest(mockClient);
    URI uri = new URI(request.getRequestLine().getUri());
    assertEquals("/v1/multi/", uri.getPath());
  }

  @Test
  public void testBody() throws IOException {
    HttpResponse response = Util.getResponse(200, "");
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Result<Nothing> result = client.writeDataPoints(wr);

    ArgumentCaptor<HttpPost> argument = ArgumentCaptor.forClass(HttpPost.class);
    verify(mockClient).execute(any(HttpHost.class), argument.capture(), any(HttpContext.class));
    assertEquals(json, EntityUtils.toString(argument.getValue().getEntity(), DEFAULT_CHARSET));
  }

  @Test
  public void testMultiStatus() throws IOException {
    HttpResponse response = Util.getResponse(207, multistatus_json);
    Client client = Util.getClient(response);

    Result<Nothing> result = client.writeDataPoints(wr);
    MultiStatus multistatus = new MultiStatus(Arrays.asList(new Status(403, Arrays.asList("Forbidden"))));

    Result<Nothing> expected = new Result<Nothing>(null, 207, "Multi-Status", multistatus);
    assertEquals(expected, result);
  }
}
