package com.tempodb;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.*;
import static org.junit.Assert.*;


public class DataPointFoundTest {

  private static final DateTime start = new DateTime(2012, 1, 1, 0, 0, 0, 0);
  private static final DateTime end1 = new DateTime(2012, 1, 1, 1, 0, 0, 0);
  private static final DateTime end2 = new DateTime(2012, 1, 1, 2, 0, 0, 0);

  private static final Interval interval1 = new Interval(start, end1);
  private static final Interval interval2 = new Interval(start, end1);
  private static final Interval interval3 = new Interval(start, end2);

  private static final DataPoint dp1 = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
  private static final DataPoint dp2 = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
  private static final DataPoint dp3 = new DataPoint(new DateTime(2012, 1, 1, 0, 1, 0, 0), 10.34);


  @Test
  public void testEquals() {
    DataPointFound dpf1 = new DataPointFound(interval1, dp1);
    DataPointFound dpf2 = new DataPointFound(interval2, dp2);
    assertEquals(dpf1, dpf2);
  }

  @Test
  public void testNotEquals_Interval() {
    DataPointFound dpf1 = new DataPointFound(interval1, dp1);
    DataPointFound dpf2 = new DataPointFound(interval3, dp2);
    assertFalse(dp1.equals(dpf2));
  }

  @Test
  public void testNotEquals_DataPoint() {
    DataPointFound dpf1 = new DataPointFound(interval1, dp1);
    DataPointFound dpf2 = new DataPointFound(interval2, dp3);
    assertFalse(dp1.equals(dpf2));
  }

  @Test
  public void testNotEquals_Null() {
    DataPointFound dpf1 = new DataPointFound(interval1, dp1);
    assertFalse(dpf1.equals(null));
  }
}

