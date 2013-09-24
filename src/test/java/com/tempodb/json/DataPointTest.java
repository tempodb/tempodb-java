package com.tempodb.json;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.*;
import static org.junit.Assert.*;

import com.tempodb.DataPoint;


public class DataPointTest {

  @Test
  public void testDeserializeUTC() throws IOException {
    DateTimeZone zone = DateTimeZone.UTC;
    DataPoint datapoint = Json.loads("{\"t\":\"2012-01-01T00:00:01.000Z\",\"v\":12.34}", DataPoint.class, zone);
    DataPoint expected = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 1, 0, zone), 12.34);
    assertEquals(expected, datapoint);
  }

  @Test
  public void testDeserializeTZ() throws IOException {
    DateTimeZone zone = DateTimeZone.forID("America/Chicago");
    DataPoint datapoint = Json.loads("{\"t\":\"2012-01-01T00:00:01.000-06:00\",\"v\":12.34}", DataPoint.class, zone);
    DataPoint expected = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 1, 0, zone), 12.34);
    assertEquals(expected, datapoint);
  }

  @Test
  public void testSerializeUTC() throws IOException {
    DateTimeZone zone = DateTimeZone.UTC;
    DataPoint datapoint = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 1, 0, zone), 12.34);

    String expected = "{\"t\":\"2012-01-01T00:00:01.000Z\",\"v\":12.34}";
    assertEquals(expected, Json.dumps(datapoint));
  }

  @Test
  public void testSerializeTZ() throws IOException {
    DateTimeZone zone = DateTimeZone.forID("America/Chicago");
    DataPoint datapoint = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 1, 0, zone), 12.34);

    String expected = "{\"t\":\"2012-01-01T00:00:01.000-06:00\",\"v\":12.34}";
    assertEquals(expected, Json.dumps(datapoint));
  }
}
