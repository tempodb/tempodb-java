package com.tempodb;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.junit.*;
import static org.junit.Assert.*;


public class DeleteAllSeriesTest {

  private static final String json = "{\"deleted\":127}";

  @Test
  public void smokeTest() throws IOException {
    HttpResponse response = Util.getResponse(200, json);
    Client client = Util.getClient(response);

    Result<DeleteSummary> result = client.deleteAllSeries();
    Result<DeleteSummary> expected = new Result<DeleteSummary>(new DeleteSummary(127), 200, "OK");
    assertEquals(expected, result);
  }

  @Test
  public void testMethod() throws IOException {
    HttpResponse response = Util.getResponse(200, json);
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Result<DeleteSummary> result = client.deleteAllSeries();

    HttpRequest request = Util.captureRequest(mockClient);
    assertEquals("DELETE", request.getRequestLine().getMethod());
  }

  @Test
  public void testUri() throws IOException, URISyntaxException {
    HttpResponse response = Util.getResponse(200, json);
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Result<DeleteSummary> result = client.deleteAllSeries();

    HttpRequest request = Util.captureRequest(mockClient);
    URI uri = new URI(request.getRequestLine().getUri());
    assertEquals("/v1/series/", uri.getPath());
  }

  @Test
  public void testParameters() throws IOException, URISyntaxException {
    HttpResponse response = Util.getResponse(200, json);
    HttpClient mockClient = Util.getMockHttpClient(response);
    Client client = Util.getClient(mockClient);

    Result<DeleteSummary> result = client.deleteAllSeries();

    HttpRequest request = Util.captureRequest(mockClient);
    URI uri = new URI(request.getRequestLine().getUri());
    List<NameValuePair> params = URLEncodedUtils.parse(uri, "UTF-8");
    assertTrue(params.contains(new BasicNameValuePair("allow_truncation", "true")));
    assertEquals(1, params.size());
  }
}
