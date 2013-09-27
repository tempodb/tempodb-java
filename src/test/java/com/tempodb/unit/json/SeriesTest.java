package com.tempodb.unit.json;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.junit.*;
import static org.junit.Assert.*;

import com.tempodb.Series;
import com.tempodb.json.Json;


public class SeriesTest {

  private static final Set<String> tags = new LinkedHashSet<String>(Arrays.asList("tag1", "tag2"));
  private static final Map<String, String> attributes;
  static {
    Map<String, String> map = new HashMap<String, String>();
    map.put("key1", "value1");
    attributes = Collections.unmodifiableMap(map);
  }

  private static final Series series = new Series("key1", "name1", tags, attributes);

  @Test
  public void testDeserialize() throws IOException {
    String json = "{\"id\":\"id1\",\"key\":\"key1\",\"name\":\"name1\",\"tags\":[\"tag1\",\"tag2\"],\"attributes\":{\"key1\":\"value1\"}}";
    Series deserialized = Json.loads(json, Series.class);
    assertEquals(series, deserialized);
  }

  @Test
  public void testSerialize() throws IOException {
    String expected = "{\"key\":\"key1\",\"name\":\"name1\",\"tags\":[\"tag1\",\"tag2\"],\"attributes\":{\"key1\":\"value1\"}}";
    String json = Json.dumps(series);
    assertEquals(expected, json);
  }
}
