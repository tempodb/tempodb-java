package com.tempodb.models;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;
import static org.junit.Assert.assertEquals;


public class SeriesTest {

  @Test
  public void testConstructor() {
    Series series = new Series("id1", "key1", "Name", new ArrayList<String>(), new HashMap<String, String>());

    assertEquals(series.getId(), "id1");
    assertEquals(series.getKey(), "key1");
    assertEquals(series.getName(), "Name");
    assertEquals(series.getTags(), new ArrayList<String>());
    assertEquals(series.getAttributes(), new HashMap<String, String>());
  }

  @Test
  public void testDefaults() {
    Series series = new Series("id1", "key1");

    assertEquals(series.getId(), "id1");
    assertEquals(series.getKey(), "key1");
    assertEquals(series.getName(), "");
    assertEquals(series.getTags(), new ArrayList<String>());
    assertEquals(series.getAttributes(), new HashMap<String, String>());
  }
}
