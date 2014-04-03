package com.tempodb;

import org.joda.time.DateTime;
import org.junit.*;
import static org.junit.Assert.*;


public class SingleValueTest {

  private static final Series series1 = new Series("key1");
  private static final Series series2 = new Series("key1");
  private static final Series series3 = new Series("key2");

  private static final DataPoint dp1 = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
  private static final DataPoint dp2 = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
  private static final DataPoint dp3 = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0), 10.34);

  @Test
  public void testEquals() {
    SingleValue sv1 = new SingleValue(series1, dp1);
    SingleValue sv2 = new SingleValue(series2, dp2);
    assertEquals(sv1, sv2);
  }

  @Test
  public void testNotEquals_Series() {
    SingleValue sv1 = new SingleValue(series1, dp1);
    SingleValue sv2 = new SingleValue(series3, dp2);
    assertFalse(sv1.equals(sv2));
  }

  @Test
  public void testNotEquals_DataPoint() {
    SingleValue sv1 = new SingleValue(series1, dp1);
    SingleValue sv2 = new SingleValue(series2, dp3);
    assertFalse(sv1.equals(sv2));
  }

  @Test
  public void testNotEquals_Null() {
    SingleValue sv1 = new SingleValue(series1, dp1);
    assertFalse(sv1.equals(null));
  }
}

