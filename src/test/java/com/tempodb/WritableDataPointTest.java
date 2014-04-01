package com.tempodb;

import org.joda.time.DateTime;
import org.junit.*;
import static org.junit.Assert.*;


public class WritableDataPointTest {

  @Test
  public void testEquals() {
    WritableDataPoint wdp1 = new WritableDataPoint(new Series("key"), new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
    WritableDataPoint wdp2 = new WritableDataPoint(new Series("key"), new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
    assertEquals(wdp1, wdp2);
  }

  @Test
  public void testNotEquals_Key() {
    WritableDataPoint wdp1 = new WritableDataPoint(new Series("key1"), new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
    WritableDataPoint wdp2 = new WritableDataPoint(new Series("key2"), new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
    assertFalse(wdp1.equals(wdp2));
  }

  @Test
  public void testNotEquals_Timestamp() {
    WritableDataPoint wdp1 = new WritableDataPoint(new Series("key"), new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
    WritableDataPoint wdp2 = new WritableDataPoint(new Series("key"), new DateTime(2012, 1, 2, 0, 0, 0, 0), 12.34);
    assertFalse(wdp1.equals(wdp2));
  }

  @Test
  public void testNotEquals_Value() {
    WritableDataPoint wdp1 = new WritableDataPoint(new Series("key"), new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
    WritableDataPoint wdp2 = new WritableDataPoint(new Series("key"), new DateTime(2012, 1, 1, 0, 0, 0, 0), 23.45);
    assertFalse(wdp1.equals(wdp2));
  }

  @Test
  public void testNotEquals_Null() {
    WritableDataPoint wdp1 = new WritableDataPoint(new Series("key"), new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
    assertFalse(wdp1.equals(null));
  }
}
