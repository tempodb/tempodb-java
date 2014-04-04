package com.tempodb.json;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.*;
import static org.junit.Assert.*;

import com.tempodb.DataPoint;
import com.tempodb.Series;
import com.tempodb.SingleValue;
import com.tempodb.SingleValueSegment;


public class SingleValueSegmentTest {

  private static final Set<String> tags = new LinkedHashSet<String>(Arrays.asList("tag1", "tag2"));
  private static final Map<String, String> attributes;
  static {
    Map<String, String> map = new HashMap<String, String>();
    map.put("key1", "value1");
    attributes = Collections.unmodifiableMap(map);
  }

  @Test
  public void testDeserialize() throws IOException {
    String json = "[{" +
      "\"series\":{" +
        "\"id\":\"id1\"," +
        "\"key\":\"key1\"," +
        "\"name\":\"name1\"," +
        "\"tags\":[\"tag1\",\"tag2\"]," +
        "\"attributes\":{\"key1\":\"value1\"}" +
      "}," +
      "\"data\":{" +
        "\"t\":\"2012-01-01T00:00:01.000Z\"," +
        "\"v\":12.34" +
      "}," +
      "\"tz\":\"UTC\"" +
    "}]";

    DateTimeZone zone = DateTimeZone.UTC;
    SingleValueSegment segment = Json.loads(json, SingleValueSegment.class);

    Series series = new Series("key1", "name1", tags, attributes);
    DataPoint datapoint = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 1, 0, zone), 12.34);
    List<SingleValue> data = Arrays.asList(new SingleValue(series, datapoint));
    SingleValueSegment expected = new SingleValueSegment(data);
    assertEquals(expected, segment);
  }
}
