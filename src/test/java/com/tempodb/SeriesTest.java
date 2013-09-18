package com.tempodb;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.junit.*;
import static org.junit.Assert.*;


public class SeriesTest {

  private static final Set<String> tags = new LinkedHashSet<String>(Arrays.asList("tag1", "tag2"));
  private static final Map<String, String> attributes;
  static {
    Map<String, String> map = new HashMap<String, String>();
    map.put("key1", "value1");
    map.put("key2", "value2");
    attributes = Collections.unmodifiableMap(map);
  }

  @Test
  public void testEquals() {
    Series s1 = new Series("id", "key", "name", tags, attributes);
    Series s2 = new Series("id", "key", "name", tags, attributes);
    assertEquals(s1, s2);
  }

  @Test
  public void testNotEquals_Id() {
    Series s1 = new Series("id1", "key", "name", tags, attributes);
    Series s2 = new Series("id2", "key", "name", tags, attributes);
    assertFalse(s1.equals(s2));
  }

  @Test
  public void testNotEquals_Key() {
    Series s1 = new Series("id", "key1", "name", tags, attributes);
    Series s2 = new Series("id", "key2", "name", tags, attributes);
    assertFalse(s1.equals(s2));
  }

  @Test
  public void testNotEquals_Name() {
    Series s1 = new Series("id", "key", "name1", tags, attributes);
    Series s2 = new Series("id", "key", "name2", tags, attributes);
    assertFalse(s1.equals(s2));
  }

  @Test
  public void testNotEquals_Tags() {
    Set<String> tags2 = new LinkedHashSet<String>();
    tags.add("tag1");

    Series s1 = new Series("id", "key", "name", tags, attributes);
    Series s2 = new Series("id", "key", "name", tags2, attributes);
    assertFalse(s1.equals(s2));
  }

  @Test
  public void testNotEquals_Attributes() {
    Map<String, String> attributes2 = new HashMap<String, String>();
    attributes2.put("key1", "value1");

    Series s1 = new Series("id", "key", "name", tags, attributes);
    Series s2 = new Series("id", "key", "name", tags, attributes2);
    assertFalse(s1.equals(s2));
  }

  @Test
  public void testNotEquals_Null() {
    Series s1 = new Series("id", "key", "name1", tags, attributes);
    assertFalse(s1.equals(null));
  }
}
