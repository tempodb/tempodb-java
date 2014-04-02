package com.tempodb.json;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.junit.*;
import static org.junit.Assert.*;

import com.tempodb.DataPoint;
import com.tempodb.DataPointFound;
import com.tempodb.DataPointFoundSegment;
import com.tempodb.Predicate;


public class DataPointFoundSegmentTest {

  @Test
  public void testDeserializeUTC() throws IOException {
    String json = "{" +
      "\"predicate\":{" +
        "\"function\":\"max\"," +
        "\"period\":\"PT1H\"" +
      "}," +
      "\"tz\":\"UTC\"," +
      "\"data\":[" +
        "{\"interval\":{\"start\":\"2012-03-28T00:00:00.000+00:00\",\"end\":\"2012-03-29T00:00:00.000+00:00\"},\"found\":{\"t\":\"2012-03-28T23:59:00.000Z\",\"v\":2879.0}}" +
        "]" +
      "}";

    DateTimeZone zone = DateTimeZone.UTC;
    DataPointFoundSegment segment = Json.loads(json, DataPointFoundSegment.class);

    Interval interval = new Interval(new DateTime(2012, 3, 28, 0, 0, 0, 0, zone), new DateTime(2012, 3, 29, 0, 0, 0, 0, zone));
    DataPoint datapoint = new DataPoint(new DateTime(2012, 3, 28, 23, 59, 0, 0, zone), 2879.0);
    List<DataPointFound> data = Arrays.asList(new DataPointFound(interval, datapoint));
    Predicate predicate = new Predicate(Period.hours(1), "max");
    DataPointFoundSegment expected = new DataPointFoundSegment(data, null, zone, predicate);
    assertEquals(expected, segment);
  }

  @Test
  public void testDeserializeTZ() throws IOException {
    String json = "{" +
      "\"predicate\":{" +
        "\"function\":\"max\"," +
        "\"period\":\"PT1H\"" +
      "}," +
      "\"tz\":\"America/Chicago\"," +
      "\"data\":[" +
        "{\"interval\":{\"start\":\"2012-03-28T00:00:00.000+00:00\",\"end\":\"2012-03-29T00:00:00.000+00:00\"},\"found\":{\"t\":\"2012-03-28T23:59:00.000-05:00\",\"v\":2879.0}}" +
        "]" +
      "}";

    DateTimeZone zone = DateTimeZone.forID("America/Chicago");
    DataPointFoundSegment segment = Json.loads(json, DataPointFoundSegment.class);

    Interval interval = new Interval(new DateTime(2012, 3, 27, 19, 0, 0, 0, zone), new DateTime(2012, 3, 28, 19, 0, 0, 0, zone));
    DataPoint datapoint = new DataPoint(new DateTime(2012, 3, 28, 23, 59, 0, 0, zone), 2879.0);
    List<DataPointFound> data = Arrays.asList(new DataPointFound(interval, datapoint));
    Predicate predicate = new Predicate(Period.hours(1), "max");
    DataPointFoundSegment expected = new DataPointFoundSegment(data, null, zone, predicate);
    assertEquals(expected, segment);
  }

  @Test
  public void testDeserializeRollupNull() throws IOException {
    String json = "{" +
      "\"predicate\":null," +
      "\"tz\":\"UTC\"," +
      "\"data\":[" +
        "{\"interval\":{\"start\":\"2012-03-28T00:00:00.000+00:00\",\"end\":\"2012-03-29T00:00:00.000+00:00\"},\"found\":{\"t\":\"2012-03-28T23:59:00.000Z\",\"v\":2879.0}}" +
        "]" +
      "}";

    DateTimeZone zone = DateTimeZone.UTC;
    DataPointFoundSegment segment = Json.loads(json, DataPointFoundSegment.class);

    Interval interval = new Interval(new DateTime(2012, 3, 28, 0, 0, 0, 0, zone), new DateTime(2012, 3, 29, 0, 0, 0, 0, zone));
    DataPoint datapoint = new DataPoint(new DateTime(2012, 3, 28, 23, 59, 0, 0, zone), 2879.0);
    List<DataPointFound> data = Arrays.asList(new DataPointFound(interval, datapoint));
    Predicate predicate = null;
    DataPointFoundSegment expected = new DataPointFoundSegment(data, null, zone, predicate);
    assertEquals(expected, segment);
  }
}
