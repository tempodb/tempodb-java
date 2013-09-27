package com.tempodb.unit.json;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.junit.*;
import static org.junit.Assert.*;

import com.tempodb.DataPoint;
import com.tempodb.DataPointSegment;
import com.tempodb.Rollup;
import com.tempodb.Fold;
import com.tempodb.json.Json;


public class DataPointSegmentTest {

  @Test
  public void testDeserializeUTC() throws IOException {
    String json = "{" +
      "\"rollup\":{" +
        "\"fold\":\"sum\"," +
        "\"period\":\"PT1H\"" +
      "}," +
      "\"tz\":\"UTC\"," +
      "\"data\":[" +
          "{\"t\":\"2012-01-01T00:00:01.000Z\",\"v\":12.34}" +
        "]" +
      "}";

    DateTimeZone zone = DateTimeZone.UTC;
    DataPointSegment segment = Json.loads(json, DataPointSegment.class);

    List<DataPoint> data = Arrays.asList(new DataPoint(new DateTime(2012, 1, 1, 0, 0, 1, 0, zone), 12.34));
    Rollup rollup = new Rollup(Period.hours(1), Fold.SUM);
    DataPointSegment expected = new DataPointSegment(data, null, zone, rollup);
    assertEquals(expected, segment);
  }

  @Test
  public void testDeserializeTZ() throws IOException {
    String json = "{" +
      "\"rollup\":{" +
        "\"fold\":\"sum\"," +
        "\"period\":\"PT1H\"" +
      "}," +
      "\"tz\":\"America/Chicago\"," +
      "\"data\":[" +
          "{\"t\":\"2012-01-01T00:00:01.000-06:00\",\"v\":12.34}" +
        "]" +
      "}";

    DateTimeZone zone = DateTimeZone.forID("America/Chicago");
    DataPointSegment segment = Json.loads(json, DataPointSegment.class);

    List<DataPoint> data = Arrays.asList(new DataPoint(new DateTime(2012, 1, 1, 0, 0, 1, 0, zone), 12.34));
    Rollup rollup = new Rollup(Period.hours(1), Fold.SUM);
    DataPointSegment expected = new DataPointSegment(data, null, zone, rollup);
    assertEquals(expected, segment);
  }

  @Test
  public void testDeserializeRollupNull() throws IOException {
    String json = "{" +
      "\"rollup\":null," +
      "\"tz\":\"UTC\"," +
      "\"data\":[" +
          "{\"t\":\"2012-01-01T00:00:01.000Z\",\"v\":12.34}" +
        "]" +
      "}";

    DateTimeZone zone = DateTimeZone.forID("UTC");
    DataPointSegment segment = Json.loads(json, DataPointSegment.class);

    List<DataPoint> data = Arrays.asList(new DataPoint(new DateTime(2012, 1, 1, 0, 0, 1, 0, zone), 12.34));
    Rollup rollup = null;
    DataPointSegment expected = new DataPointSegment(data, null, zone, rollup);
    assertEquals(expected, segment);
  }
}
