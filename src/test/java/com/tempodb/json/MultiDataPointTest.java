package com.tempodb.json;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.*;
import static org.junit.Assert.*;
import org.junit.rules.ExpectedException;

import com.tempodb.MultiDataPoint;


public class MultiDataPointTest {
  private static final DateTimeZone timezone = DateTimeZone.UTC;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testDeserialize_Id() throws IOException {
    MultiDataPoint datapoint = Json.loads("{\"id\":\"id1\",\"t\":1325376001000,\"v\":12.34}", MultiDataPoint.class, timezone);
    MultiDataPoint expected = MultiDataPoint.forId("id1", new DateTime(2012, 1, 1, 0, 0, 1, 0, timezone), 12.34);
    assertEquals(expected, datapoint);
  }

  @Test
  public void testDeserialize_Key() throws IOException {
    MultiDataPoint datapoint = Json.loads("{\"key\":\"key1\",\"t\":1325376001000,\"v\":12.34}", MultiDataPoint.class, timezone);
    MultiDataPoint expected = MultiDataPoint.forKey("key1", new DateTime(2012, 1, 1, 0, 0, 1, 0, timezone), 12.34);
    assertEquals(expected, datapoint);
  }

  @Test
  public void testDeserialize_Both() throws IOException {
    MultiDataPoint datapoint = Json.loads("{\"id\":\"id1\",\"key\":\"key1\",\"t\":1325376001000,\"v\":12.34}", MultiDataPoint.class, timezone);
    MultiDataPoint expected = MultiDataPoint.forId("id1", new DateTime(2012, 1, 1, 0, 0, 1, 0, timezone), 12.34);
    assertEquals(expected, datapoint);
  }

  @Test
  public void testDeserialize_MissingIdKey() throws IOException {
    thrown.expect(JsonMappingException.class);
    MultiDataPoint datapoint = Json.loads("{\"t\":1325376001000,\"v\":12.34}", MultiDataPoint.class, timezone);
  }

  @Test
  public void testDeserialize_NullId() throws IOException {
    thrown.expect(JsonMappingException.class);
    MultiDataPoint datapoint = Json.loads("{\"id\":null,\"t\":1325376001000,\"v\":12.34}", MultiDataPoint.class, timezone);
  }

  @Test
  public void testDeserialize_NullKey() throws IOException {
    thrown.expect(JsonMappingException.class);
    MultiDataPoint datapoint = Json.loads("{\"key\":null,\"t\":1325376001000,\"v\":12.34}", MultiDataPoint.class, timezone);
  }

  @Test
  public void testDeserialize_StringValue() throws IOException {
    thrown.expect(JsonMappingException.class);
    MultiDataPoint datapoint = Json.loads("{\"key\":\"key1\",\"t\":1325376001000,\"v\":\"12.34\"}", MultiDataPoint.class, timezone);
  }

  @Test
  public void testSerialize_Id() throws IOException {
    DateTimeZone zone = DateTimeZone.UTC;
    MultiDataPoint datapoint = MultiDataPoint.forId("id1",new DateTime(2012, 1, 1, 0, 0, 1, 0, timezone), 12.34);

    String expected = "{\"id\":\"id1\",\"t\":1325376001000,\"v\":12.34}";
    assertEquals(expected, Json.dumps(datapoint));
  }

  @Test
  public void testSerialize_Key() throws IOException {
    DateTimeZone zone = DateTimeZone.UTC;
    MultiDataPoint datapoint = MultiDataPoint.forKey("key1",new DateTime(2012, 1, 1, 0, 0, 1, 0, timezone), 12.34);

    String expected = "{\"key\":\"key1\",\"t\":1325376001000,\"v\":12.34}";
    assertEquals(expected, Json.dumps(datapoint));
  }
}
