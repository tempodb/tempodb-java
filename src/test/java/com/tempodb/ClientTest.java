package com.tempodb;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.http.*;
import org.apache.http.message.BasicHttpRequest;
import org.junit.*;
import static org.junit.Assert.*;
import org.junit.rules.ExpectedException;


public class ClientTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testConstructor() throws UnknownHostException {
    Client client = new Client(new Database("id"), new Credentials("key", "secret"), InetAddress.getByName("example.com"), 10, "http");
    assertNotNull(client);
  }

  @Test
  public void testInvalidScheme() throws UnknownHostException {
    thrown.expect(IllegalArgumentException.class);
    Client client = new Client(new Database("id"), new Credentials("key", "secret"), InetAddress.getByName("example.com"), 10, "scheme");
  }
}
