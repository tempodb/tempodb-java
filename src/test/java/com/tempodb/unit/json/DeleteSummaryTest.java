package com.tempodb.unit.json;

import java.io.IOException;

import org.junit.*;
import static org.junit.Assert.*;

import com.tempodb.DeleteSummary;
import com.tempodb.json.Json;


public class DeleteSummaryTest {

  @Test
  public void testDeserialize() throws IOException {
    String json = "{\"deleted\":1}";
    DeleteSummary expected = new DeleteSummary(1);
    assertEquals(expected, Json.loads(json, DeleteSummary.class));
  }

  @Test
  public void testSerialize() throws IOException {
    DeleteSummary summary = new DeleteSummary(2);
    String expected = "{\"deleted\":2}";
    assertEquals(expected, Json.dumps(summary));
  }
}
