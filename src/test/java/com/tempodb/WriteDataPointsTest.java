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


public class WriteDataPointsTest {

  private static final String json = "[" +
    "{\"id\":\"id1\",\"t\":1332824400000,\"v\":12.34}," +
    "{\"key\":\"key1\",\"t\":1332824460000,\"v\":23.45}" +
  "]";

  private static final DateTimeZone timezone = DateTimeZone.UTC;
  private static final List<MultiDataPoint> data = Arrays.asList(MultiDataPoint.forId("id1", new DateTime(2012, 3, 27, 5, 0, 0, 0, timezone), 12.34),
                                                                 MultiDataPoint.forKey("key1", new DateTime(2012, 3, 27, 5, 1, 0, 0, timezone), 23.45));
  private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

  @Test
  public void smokeTest() throws IOException {
    HttpResponse response = Util.getResponse(207, "");
    Client client = Util.getClient(response);
    Result<Nothing> result = client.writeDataPoints(data);

    assertTrue(result.isSuccessful());
  }

  @Test
  public void testMethod() throws IOException {
    HttpResponse response = Util.getResponse(207, "");
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Result<Nothing> result = client.writeDataPoints(data);

    ArgumentCaptor<HttpRequest> argument = ArgumentCaptor.forClass(HttpRequest.class);
    verify(mockClient).execute(any(HttpHost.class), argument.capture());
    assertEquals("POST", argument.getValue().getRequestLine().getMethod());
  }

  @Test
  public void testUri() throws IOException, URISyntaxException {
    HttpResponse response = Util.getResponse(207, "");
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Result<Nothing> result = client.writeDataPoints(data);

    ArgumentCaptor<HttpRequest> argument = ArgumentCaptor.forClass(HttpRequest.class);
    verify(mockClient).execute(any(HttpHost.class), argument.capture());

    URI uri = new URI(argument.getValue().getRequestLine().getUri());
    assertEquals("/v1/multi/", argument.getValue().getRequestLine().getUri());
  }

  @Test
  public void testBody() throws IOException {
    HttpResponse response = Util.getResponse(207, "");
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Result<Nothing> result = client.writeDataPoints(data);

    ArgumentCaptor<HttpPost> argument = ArgumentCaptor.forClass(HttpPost.class);
    verify(mockClient).execute(any(HttpHost.class), argument.capture());
    assertEquals(json, EntityUtils.toString(argument.getValue().getEntity(), DEFAULT_CHARSET));
  }

  @Test
  public void testError() throws IOException {
    HttpResponse response = Util.getResponse(403, "Forbidden");
    Client client = Util.getClient(response);
    Result<Nothing> result = client.writeDataPoints(data);

    assertFalse(result.isSuccessful());
  }
}
