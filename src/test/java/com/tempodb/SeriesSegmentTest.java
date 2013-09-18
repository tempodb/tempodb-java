package com.tempodb;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;


public class SeriesSegmentTest {

  private static final List<Series> data1 = Arrays.asList(new Series("id", "key", "name", new HashSet(), new HashMap()));
  private static final List<Series> data2 = Arrays.asList(new Series("id", "key", "name", new HashSet(), new HashMap()));
  private static final List<Series> data3 = Arrays.asList(new Series("id3", "key3", "name3", new HashSet(), new HashMap()));

  @Test
  public void testEquals() {
    SeriesSegment segment1 = new SeriesSegment(data1, null);
    SeriesSegment segment2 = new SeriesSegment(data2, null);
    assertEquals(segment1, segment2);
  }

  @Test
  public void testNotEquals_Data() {
    SeriesSegment segment1 = new SeriesSegment(data1, null);
    SeriesSegment segment2 = new SeriesSegment(data3, null);
    assertFalse(segment1.equals(segment2));
  }

  @Test
  public void testNotEquals_Next() {
    SeriesSegment segment1 = new SeriesSegment(data1, "next1");
    SeriesSegment segment2 = new SeriesSegment(data2, "next2");
    assertFalse(segment1.equals(segment2));
  }

  @Test
  public void testNotEquals_Null() {
    SeriesSegment segment1 = new SeriesSegment(data1, "next1");
    assertFalse(segment1.equals(null));
  }
}
