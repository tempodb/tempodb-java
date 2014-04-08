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
import com.tempodb.MultiRollup;
import com.tempodb.MultiRollupDataPointSegment;


public class MultiRollupDataPointSegmentTest {

  private static final Map<String, Number> data1;
  private static final Map<String, Number> data2;
  private static final Map<String, Number> data3;

  static {
    data1 = new HashMap<String, Number>();
    data1.put("sum", 12.34);
    data1.put("max", 23.45);

    data2 = new HashMap<String, Number>();
    data2.put("sum", 23.45);
    data2.put("max", 34.56);

    data3 = new HashMap<String, Number>();
    data3.put("sum", 34.56);
    data3.put("max", 45.67);
  }

  @Test
  public void testDeserializeUTC() throws IOException {
    String json = "{" +
      "\"rollup\":{" +
        "\"folds\":[\"sum\",\"max\"]," +
        "\"period\":\"PT1H\"" +
      "}," +
      "\"tz\":\"UTC\"," +
      "\"data\":[" +
          "{\"t\":\"2012-01-01T00:00:00.000Z\",\"v\":{\"sum\":12.34,\"max\":23.45}}," +
          "{\"t\":\"2012-01-01T01:00:00.000Z\",\"v\":{\"sum\":23.45,\"max\":34.56}}," +
          "{\"t\":\"2012-01-01T02:00:00.000Z\",\"v\":{\"sum\":34.56,\"max\":45.67}}" +
        "]" +
      "}";

    DateTimeZone zone = DateTimeZone.UTC;
    MultiRollupDataPointSegment segment = Json.loads(json, MultiRollupDataPointSegment.class);

    List<MultiDataPoint> data = Arrays.asList(
      new MultiDataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0, zone), data1),
      new MultiDataPoint(new DateTime(2012, 1, 1, 1, 0, 0, 0, zone), data2),
      new MultiDataPoint(new DateTime(2012, 1, 1, 2, 0, 0, 0, zone), data3)
    );
    MultiRollup rollup = new MultiRollup(Period.hours(1), new Fold[] { Fold.SUM, Fold.MAX });
    MultiRollupDataPointSegment expected = new MultiRollupDataPointSegment(data, null, zone, rollup);
    assertEquals(expected, segment);
  }

  @Test
  public void testDeserializeTZ() throws IOException {
    String json = "{" +
      "\"rollup\":{" +
        "\"folds\":[\"sum\",\"max\"]," +
        "\"period\":\"PT1H\"" +
      "}," +
      "\"tz\":\"America/Chicago\"," +
      "\"data\":[" +
          "{\"t\":\"2012-01-01T00:00:00.000-06:00\",\"v\":{\"sum\":12.34,\"max\":23.45}}," +
          "{\"t\":\"2012-01-01T01:00:00.000-06:00\",\"v\":{\"sum\":23.45,\"max\":34.56}}," +
          "{\"t\":\"2012-01-01T02:00:00.000-06:00\",\"v\":{\"sum\":34.56,\"max\":45.67}}" +
        "]" +
      "}";

    DateTimeZone zone = DateTimeZone.forID("America/Chicago");
    MultiRollupDataPointSegment segment = Json.loads(json, MultiRollupDataPointSegment.class);

    List<MultiDataPoint> data = Arrays.asList(
      new MultiDataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0, zone), data1),
      new MultiDataPoint(new DateTime(2012, 1, 1, 1, 0, 0, 0, zone), data2),
      new MultiDataPoint(new DateTime(2012, 1, 1, 2, 0, 0, 0, zone), data3)
    );
    MultiRollup rollup = new MultiRollup(Period.hours(1), new Fold[] { Fold.SUM, Fold.MAX });
    MultiRollupDataPointSegment expected = new MultiRollupDataPointSegment(data, null, zone, rollup);
    assertEquals(expected, segment);
  }
}
