package com.tempodb;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.*;
import static org.junit.Assert.*;


public class SingleValueSegmentTest {

  private static final DataPoint dp1 = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
  private static final DataPoint dp2 = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
  private static final DataPoint dp3 = new DataPoint(new DateTime(2012, 1, 2, 0, 0, 0, 0), 10.34);

  private static final List<SingleValue> data1 = Arrays.asList(new SingleValue(new Series("key"), dp1));
  private static final List<SingleValue> data2 = Arrays.asList(new SingleValue(new Series("key"), dp2));
  private static final List<SingleValue> data3 = Arrays.asList(new SingleValue(new Series("key3"), dp3));

  @Test
  public void testEquals() {
    SingleValueSegment segment1 = new SingleValueSegment(data1);
    SingleValueSegment segment2 = new SingleValueSegment(data2);
    assertEquals(segment1, segment2);
  }

  @Test
  public void testNotEquals_Data() {
    SingleValueSegment segment1 = new SingleValueSegment(data1, null);
    SingleValueSegment segment2 = new SingleValueSegment(data3, null);
    assertFalse(segment1.equals(segment2));
  }

  @Test
  public void testNotEquals_Next() {
    SingleValueSegment segment1 = new SingleValueSegment(data1, "next1");
    SingleValueSegment segment2 = new SingleValueSegment(data2, "next2");
    assertFalse(segment1.equals(segment2));
  }

  @Test
  public void testNotEquals_Null() {
    SingleValueSegment segment1 = new SingleValueSegment(data1, "next1");
    assertFalse(segment1.equals(null));
  }
}
