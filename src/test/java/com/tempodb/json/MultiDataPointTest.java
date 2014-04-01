package com.tempodb.json;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.*;
import static org.junit.Assert.*;

import com.tempodb.MultiDataPoint;


public class MultiDataPointTest {

  private static Map<String, Number> data;
  static {
    data = new HashMap<String, Number>();
    data.put("key1", 12.34);
    data.put("key2", 23.45);
  }

  @Test
  public void testDeserializeUTC() throws IOException {
    String json = "{\"t\":\"2012-01-01T00:00:00.000Z\",\"v\":{\"key2\":23.45,\"key1\":12.34}}";
    DateTimeZone zone = DateTimeZone.UTC;
    MultiDataPoint datapoint = Json.loads(json, MultiDataPoint.class, zone);

    MultiDataPoint expected = new MultiDataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0, zone), data);
    assertEquals(expected, datapoint);
  }

  @Test
  public void testDeserializeTZ() throws IOException {
    String json = "{\"t\":\"2012-01-01T00:00:00.000-06:00\",\"v\":{\"key2\":23.45,\"key1\":12.34}}";
    DateTimeZone zone = DateTimeZone.forID("America/Chicago");
    MultiDataPoint datapoint = Json.loads(json, MultiDataPoint.class, zone);
    MultiDataPoint expected = new MultiDataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0, zone), data);
    assertEquals(expected, datapoint);
  }
}
