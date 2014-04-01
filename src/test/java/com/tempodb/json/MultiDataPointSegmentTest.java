package com.tempodb.json;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.junit.*;
import static org.junit.Assert.*;

import com.tempodb.Fold;
import com.tempodb.MultiDataPoint;
import com.tempodb.MultiDataPointSegment;
import com.tempodb.Rollup;


public class MultiDataPointSegmentTest {

  private static final Map<String, Number> data1;
  private static final Map<String, Number> data2;
  private static final Map<String, Number> data3;

  static {
    data1 = new HashMap<String, Number>();
    data1.put("key1", 12.34);
    data1.put("key2", 23.45);

    data2 = new HashMap<String, Number>();
    data2.put("key1", 23.45);
    data2.put("key2", 34.56);

    data3 = new HashMap<String, Number>();
    data3.put("key1", 34.56);
    data3.put("key2", 45.67);
  }

  @Test
  public void testDeserializeUTC() throws IOException {
    String json = "{" +
      "\"rollup\":{" +
        "\"fold\":\"sum\"," +
        "\"period\":\"PT1H\"" +
      "}," +
      "\"tz\":\"UTC\"," +
      "\"data\":[" +
          "{\"t\":\"2012-01-01T00:00:00.000Z\",\"v\":{\"key1\":12.34,\"key2\":23.45}}," +
          "{\"t\":\"2012-01-01T01:00:00.000Z\",\"v\":{\"key1\":23.45,\"key2\":34.56}}," +
          "{\"t\":\"2012-01-01T02:00:00.000Z\",\"v\":{\"key1\":34.56,\"key2\":45.67}}" +
        "]" +
      "}";

    DateTimeZone zone = DateTimeZone.UTC;
    MultiDataPointSegment segment = Json.loads(json, MultiDataPointSegment.class);

    List<MultiDataPoint> data = Arrays.asList(
      new MultiDataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0, zone), data1),
      new MultiDataPoint(new DateTime(2012, 1, 1, 1, 0, 0, 0, zone), data2),
      new MultiDataPoint(new DateTime(2012, 1, 1, 2, 0, 0, 0, zone), data3)
    );
    Rollup rollup = new Rollup(Period.hours(1), Fold.SUM);
    MultiDataPointSegment expected = new MultiDataPointSegment(data, null, zone, rollup);
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
          "{\"t\":\"2012-01-01T00:00:00.000-06:00\",\"v\":{\"key1\":12.34,\"key2\":23.45}}," +
          "{\"t\":\"2012-01-01T01:00:00.000-06:00\",\"v\":{\"key1\":23.45,\"key2\":34.56}}," +
          "{\"t\":\"2012-01-01T02:00:00.000-06:00\",\"v\":{\"key1\":34.56,\"key2\":45.67}}" +
        "]" +
      "}";

    DateTimeZone zone = DateTimeZone.forID("America/Chicago");
    MultiDataPointSegment segment = Json.loads(json, MultiDataPointSegment.class);

    List<MultiDataPoint> data = Arrays.asList(
      new MultiDataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0, zone), data1),
      new MultiDataPoint(new DateTime(2012, 1, 1, 1, 0, 0, 0, zone), data2),
      new MultiDataPoint(new DateTime(2012, 1, 1, 2, 0, 0, 0, zone), data3)
    );
    Rollup rollup = new Rollup(Period.hours(1), Fold.SUM);
    MultiDataPointSegment expected = new MultiDataPointSegment(data, null, zone, rollup);
    assertEquals(expected, segment);
  }

  @Test
  public void testDeserializeRollupNull() throws IOException {
    String json = "{" +
      "\"rollup\":null," +
      "\"tz\":\"UTC\"," +
      "\"data\":[" +
          "{\"t\":\"2012-01-01T00:00:00.000Z\",\"v\":{\"key1\":12.34,\"key2\":23.45}}," +
          "{\"t\":\"2012-01-01T01:00:00.000Z\",\"v\":{\"key1\":23.45,\"key2\":34.56}}," +
          "{\"t\":\"2012-01-01T02:00:00.000Z\",\"v\":{\"key1\":34.56,\"key2\":45.67}}" +
        "]" +
      "}";

    DateTimeZone zone = DateTimeZone.forID("UTC");
    MultiDataPointSegment segment = Json.loads(json, MultiDataPointSegment.class);

    List<MultiDataPoint> data = Arrays.asList(
      new MultiDataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0, zone), data1),
      new MultiDataPoint(new DateTime(2012, 1, 1, 1, 0, 0, 0, zone), data2),
      new MultiDataPoint(new DateTime(2012, 1, 1, 2, 0, 0, 0, zone), data3)
    );
    Rollup rollup = null;
    MultiDataPointSegment expected = new MultiDataPointSegment(data, null, zone, rollup);
    assertEquals(expected, segment);
  }
}
