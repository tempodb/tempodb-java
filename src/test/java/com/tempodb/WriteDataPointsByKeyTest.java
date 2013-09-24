package com.tempodb;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;


public class WriteDataPointsByKeyTest {

  private static final String json = "[" +
    "{\"t\":\"2012-03-27T05:00:00.000Z\",\"v\":12.34}," +
    "{\"t\":\"2012-03-27T05:01:00.000Z\",\"v\":23.45}" +
  "]";

  private static final DateTimeZone timezone = DateTimeZone.UTC;
  private static final List<DataPoint> data = Arrays.asList(new DataPoint(new DateTime(2012, 3, 27, 5, 0, 0, 0, timezone), 12.34),
                                                            new DataPoint(new DateTime(2012, 3, 27, 5, 1, 0, 0, timezone), 23.45));
  private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

  @Test
  public void smokeTest() throws IOException {
    HttpResponse response = Util.getResponse(200, "");
    Client client = Util.getClient(response);
    Result<Nothing> result = client.writeDataPointsByKey("key1", data);
    assertTrue(result.isSuccessful());
  }

  @Test
  public void testMethod() throws IOException {
    HttpResponse response = Util.getResponse(200, "");
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Result<Nothing> result = client.writeDataPointsByKey("key1", data);

    ArgumentCaptor<HttpRequest> argument = ArgumentCaptor.forClass(HttpRequest.class);
    verify(mockClient).execute(any(HttpHost.class), argument.capture());
    assertEquals("POST", argument.getValue().getRequestLine().getMethod());
  }

  @Test
  public void testUri() throws IOException, URISyntaxException {
    HttpResponse response = Util.getResponse(200, "");
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Result<Nothing> result = client.writeDataPointsByKey("key1", data);

    ArgumentCaptor<HttpRequest> argument = ArgumentCaptor.forClass(HttpRequest.class);
    verify(mockClient).execute(any(HttpHost.class), argument.capture());

    URI uri = new URI(argument.getValue().getRequestLine().getUri());
    assertEquals("/v1/series/key/key1/data/", uri.getPath());
  }

  @Test
  public void testBody() throws IOException {
    HttpResponse response = Util.getResponse(200, "");
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Result<Nothing> result = client.writeDataPointsByKey("key1", data);

    ArgumentCaptor<HttpPost> argument = ArgumentCaptor.forClass(HttpPost.class);
    verify(mockClient).execute(any(HttpHost.class), argument.capture());
    assertEquals(json, EntityUtils.toString(argument.getValue().getEntity(), DEFAULT_CHARSET));
  }

  @Test
  public void testError() throws IOException {
    HttpResponse response = Util.getResponse(403, "Forbidden");
    Client client = Util.getClient(response);
    Result<Nothing> result = client.writeDataPointsByKey("key1", data);
    assertFalse(result.isSuccessful());
  }
}
