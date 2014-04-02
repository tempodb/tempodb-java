package com.tempodb.json;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.junit.*;
import static org.junit.Assert.*;

import com.tempodb.DataPoint;
import com.tempodb.DataPointFound;


public class DataPointFoundTest {

  @Test
  public void testDeserializeUTC() throws IOException {
    String json = "{\"interval\":{\"start\":\"2012-03-28T00:00:00.000Z\",\"end\":\"2012-03-29T00:00:00.000Z\"},\"found\":{\"t\":\"2012-03-28T23:59:00.000Z\",\"v\":2879.0}}";
    DateTimeZone zone = DateTimeZone.UTC;
    DataPointFound found = Json.loads(json, DataPointFound.class, zone);
    Interval interval = new Interval(new DateTime(2012, 3, 28, 0, 0, 0, 0, zone), new DateTime(2012, 3, 29, 0, 0, 0, 0, zone));
    DataPoint datapoint = new DataPoint(new DateTime(2012, 3, 28, 23, 59, 0, 0, zone), 2879.0);
    DataPointFound expected = new DataPointFound(interval, datapoint);
    assertEquals(expected, found);
  }

  @Test
  public void testDeserializeTZ() throws IOException {
    String json = "{\"interval\":{\"start\":\"2012-03-28T00:00:00.000Z\",\"end\":\"2012-03-29T00:00:00.000Z\"},\"found\":{\"t\":\"2012-03-28T23:59:00.000Z\",\"v\":2879.0}}";
    DateTimeZone zone = DateTimeZone.forID("America/Chicago");
    DataPointFound found = Json.loads(json, DataPointFound.class, zone);
    Interval interval = new Interval(new DateTime(2012, 3, 27, 19, 0, 0, 0, zone), new DateTime(2012, 3, 28, 19, 0, 0, 0, zone));
    DataPoint datapoint = new DataPoint(new DateTime(2012, 3, 28,  18, 59, 0, 0, zone), 2879.0);
    DataPointFound expected = new DataPointFound(interval, datapoint);
    assertEquals(expected, found);
  }
}
