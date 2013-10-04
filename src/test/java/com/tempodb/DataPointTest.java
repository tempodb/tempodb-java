package com.tempodb;

import org.joda.time.DateTime;
import org.junit.*;
import static org.junit.Assert.*;


public class DataPointTest {

  @Test
  public void testEquals() {
    DataPoint dp1 = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
    DataPoint dp2 = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
    assertEquals(dp1, dp2);
  }

  @Test
  public void testNotEquals_DateTime() {
    DataPoint dp1 = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
    DataPoint dp2 = new DataPoint(new DateTime(2012, 1, 2, 0, 0, 0, 0), 12.34);
    assertFalse(dp1.equals(dp2));
  }

  @Test
  public void testNotEquals_Value() {
    DataPoint dp1 = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
    DataPoint dp2 = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0), 12);
    assertFalse(dp1.equals(dp2));
  }

  @Test
  public void testNotEquals_Null() {
    DataPoint dp1 = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
    assertFalse(dp1.equals(null));
  }
}
