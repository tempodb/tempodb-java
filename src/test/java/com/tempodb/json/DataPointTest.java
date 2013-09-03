package com.tempodb.json;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.*;
import static org.junit.Assert.*;

import com.tempodb.DataPoint;


public class DataPointTest {

  @Test
  public void testDeserializeUTC() throws IOException {
    DateTimeZone zone = DateTimeZone.UTC;
    ObjectMapper mapper = Json.newObjectMapper();
    mapper.setTimeZone(zone.toTimeZone());
    DataPoint datapoint = mapper.readValue("{\"t\":\"2012-01-01T00:00:01.000+00:00\",\"v\":12.34}", DataPoint.class);
    DataPoint expected = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 1, 0, zone), 12.34);
    assertEquals(expected, datapoint);
  }

  @Test
  public void testDeserializeTZ() throws IOException {
    DateTimeZone zone = DateTimeZone.forID("America/Chicago");
    ObjectMapper mapper = Json.newObjectMapper();
    mapper.setTimeZone(zone.toTimeZone());
    DataPoint datapoint = mapper.readValue("{\"t\":\"2012-01-01T00:00:01.000-06:00\",\"v\":12.34}", DataPoint.class);
    DataPoint expected = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 1, 0, zone), 12.34);
    assertEquals(expected, datapoint);
  }

  @Test
  public void testSerializeUTC() throws IOException {
    DateTimeZone zone = DateTimeZone.UTC;
    DataPoint datapoint = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 1, 0, zone), 12.34);

    ObjectMapper mapper = Json.newObjectMapper();
    mapper.setTimeZone(zone.toTimeZone());

    String expected = "{\"t\":\"2012-01-01T00:00:01.000Z\",\"v\":12.34}";
    assertEquals(expected, mapper.writeValueAsString(datapoint));
  }

  @Test
  public void testSerializeTZ() throws IOException {
    DateTimeZone zone = DateTimeZone.forID("America/Chicago");
    DataPoint datapoint = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 1, 0, zone), 12.34);

    ObjectMapper mapper = Json.newObjectMapper();
    mapper.setTimeZone(zone.toTimeZone());

    String expected = "{\"t\":\"2012-01-01T00:00:01.000-06:00\",\"v\":12.34}";
    assertEquals(expected, mapper.writeValueAsString(datapoint));
  }
}
