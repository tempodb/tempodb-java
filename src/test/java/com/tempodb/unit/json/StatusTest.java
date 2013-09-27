package com.tempodb.unit.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.*;
import static org.junit.Assert.*;

import com.tempodb.Status;
import com.tempodb.json.Json;


public class StatusTest {

  @Test
  public void testDeserialize() throws IOException {
    String json = "{\"status\":1,\"messages\":[\"message1\"]}";
    Status expected = new Status(1, Arrays.asList("message1"));
    assertEquals(expected, Json.loads(json, Status.class));
  }

  @Test
  public void testDeserialize_EmptyMessages() throws IOException {
    String json = "{\"status\":1,\"messages\":[]}";
    Status expected = new Status(1, new ArrayList<String>());
    assertEquals(expected, Json.loads(json, Status.class));
  }

  @Test
  public void testSerialize() throws IOException {
    Status status = new Status(200, Arrays.asList("message1", "message2"));
    String expected = "{\"status\":200,\"messages\":[\"message1\",\"message2\"]}";
    assertEquals(expected, Json.dumps(status));
  }
}
