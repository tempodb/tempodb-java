package com.tempodb;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.*;
import static org.junit.Assert.*;


public class MultiDataPointTest {

  private static final Map<String, Number> data1;
  private static final Map<String, Number> data2;
  private static final Map<String, Number> data3;

  static {
    data1 = new HashMap<String, Number>();
    data1.put("key1", 12.34);
    data1.put("key2", 23.45);

    data2 = new HashMap<String, Number>();
    data2.put("key1", 12.34);
    data2.put("key2", 23.45);

    data3 = new HashMap<String, Number>();
    data3.put("key1", 12.34);
  }

  @Test
  public void testEquals() {
    MultiDataPoint mdp1 = new MultiDataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0), data1);
    MultiDataPoint mdp2 = new MultiDataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0), data2);
    assertEquals(mdp1, mdp2);
  }

  @Test
  public void testNotEquals_DateTime() {
    MultiDataPoint mdp1 = new MultiDataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0), data1);
    MultiDataPoint mdp2 = new MultiDataPoint(new DateTime(2012, 1, 2, 0, 0, 0, 0), data2);
    assertFalse(mdp1.equals(mdp2));
  }

  @Test
  public void testNotEquals_Data() {
    MultiDataPoint mdp1 = new MultiDataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0), data1);
    MultiDataPoint mdp2 = new MultiDataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0), data3);
    assertFalse(mdp1.equals(mdp2));
  }

  @Test
  public void testNotEquals_Null() {
    MultiDataPoint mdp1 = new MultiDataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0), data1);
    assertFalse(mdp1.equals(null));
  }

  @Test
  public void testGetMissingIsNull() {
    MultiDataPoint mdp1 = new MultiDataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0), data1);
    assertEquals(mdp1.get("blah"), null);
  }
}
