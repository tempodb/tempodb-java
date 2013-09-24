package com.tempodb.json;

import java.io.IOException;
import java.util.Arrays;

import org.junit.*;
import static org.junit.Assert.*;

import com.tempodb.MultiStatus;
import com.tempodb.Status;


public class MultiStatusTest {

  @Test
  public void testDeserialize() throws IOException {
    String json = "{\"multistatus\":[{\"status\":1,\"messages\":[\"message1\"]}]}";
    MultiStatus expected = new MultiStatus(Arrays.asList(new Status(1, Arrays.asList("message1"))));
    assertEquals(expected, Json.loads(json, MultiStatus.class));
  }

  @Test
  public void testSerialize() throws IOException {
    MultiStatus multistatus = new MultiStatus(Arrays.asList(new Status(200, Arrays.asList("message1", "message2"))));
    String expected = "{\"multistatus\":[{\"status\":200,\"messages\":[\"message1\",\"message2\"]}]}";
    assertEquals(expected, Json.dumps(multistatus));
  }
}
