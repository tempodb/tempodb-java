package com.tempodb.unit;

import java.io.IOException;

import org.apache.http.*;
import org.apache.http.message.BasicHttpRequest;
import org.junit.*;
import static org.junit.Assert.*;

import com.tempodb.Client;


public class ClientTest {

  @Test
  public void testConstructor() {
    Client client = new Client("key", "secret", "example.com", 10, false);
    assertNotNull(client);
  }
}
