package com.tempodb;

import java.io.IOException;

import org.apache.http.*;
import org.apache.http.message.BasicHttpRequest;
import org.junit.*;
import static org.junit.Assert.*;


public class ClientTest {

  @Test
  public void testConstructor() {
    Client client = new Client("key", "secret", "example.com", 10, false);
    assertNotNull(client);
  }

  @Test
  public void testMock() throws IOException {
    HttpResponse expected = Util.getResponse(200, "");
    Client client = Util.getClient(expected);
    HttpRequest request = new BasicHttpRequest("GET", "/");

    HttpResponse response = client.execute(request);
    assertEquals(expected, response);
  }
}
