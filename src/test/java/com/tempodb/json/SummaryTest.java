package com.tempodb.json;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.junit.*;
import static org.junit.Assert.*;

import com.tempodb.Series;
import com.tempodb.Summary;


public class SummaryTest {

  @Test
  public void testDeserialize() throws IOException {
    String json = "{" +
      "\"summary\":{" +
        "\"max\":12.34," +
        "\"min\":23.45" +
      "}," +
      "\"tz\":\"UTC\"," +
      "\"start\":\"2012-01-01T00:00:00.000Z\"," +
      "\"end\":\"2012-01-02T00:00:00.000Z\"," +
      "\"series\":{" +
        "\"id\":\"id1\"," +
        "\"key\":\"key1\"," +
        "\"name\":\"\"," +
        "\"tags\":[]," +
        "\"attributes\":{}" +
      "}" +
    "}";

    DateTimeZone zone = DateTimeZone.UTC;
    DateTime start = new DateTime(2012, 1, 1, 0, 0, 0, 0, zone);
    DateTime end = new DateTime(2012, 1, 2, 0, 0, 0, 0, zone);

    Map<String, Number> data = new HashMap<String, Number>();
    data.put("max", 12.34);
    data.put("min", 23.45);

    Series series = new Series("key1");

    Summary summary = Json.loads(json, Summary.class);
    Summary expected = new Summary(series, new Interval(start, end), data);
    assertEquals(expected, summary);
  }

  @Test
  public void testDeserializeTz() throws IOException {
    String json = "{" +
      "\"summary\":{" +
        "\"max\":12.34," +
        "\"min\":23.45" +
      "}," +
      "\"tz\":\"America/Chicago\"," +
      "\"start\":\"2012-01-01T00:00:00.000-06:00\"," +
      "\"end\":\"2012-01-02T00:00:00.000-06:00\"," +
      "\"series\":{" +
        "\"id\":\"id1\"," +
        "\"key\":\"key1\"," +
        "\"name\":\"\"," +
        "\"tags\":[]," +
        "\"attributes\":{}" +
      "}" +
    "}";

    DateTimeZone zone = DateTimeZone.forID("America/Chicago");
    DateTime start = new DateTime(2012, 1, 1, 0, 0, 0, 0, zone);
    DateTime end = new DateTime(2012, 1, 2, 0, 0, 0, 0, zone);

    Map<String, Number> data = new HashMap<String, Number>();
    data.put("max", 12.34);
    data.put("min", 23.45);

    Series series = new Series("key1");

    Summary summary = Json.loads(json, Summary.class);
    Summary expected = new Summary(series, new Interval(start, end), data);
    assertEquals(expected, summary);
  }
}
