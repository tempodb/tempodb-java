package com.tempodb.unit;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.*;
import static org.junit.Assert.*;

import com.tempodb.DataPoint;
import com.tempodb.Series;
import com.tempodb.WriteRequest;


public class WriteRequestTest {
  private static final DateTimeZone timezone = DateTimeZone.UTC;

  private static final Series series1 = new Series("key1");
  private static final Series series2 = new Series("key1");
  private static final Series series3 = new Series("key2");

  private static final DataPoint dp1 = new DataPoint(new DateTime(2102, 1, 1, 0, 0, 0, 0, timezone), 12.34);
  private static final DataPoint dp2 = new DataPoint(new DateTime(2102, 1, 1, 0, 0, 0, 0, timezone), 12.34);
  private static final DataPoint dp3 = new DataPoint(new DateTime(2102, 1, 1, 0, 0, 0, 0, timezone), 23.45);

  @Test
  public void testEquals() {
    WriteRequest wr1 = new WriteRequest().add(series1, dp1);
    WriteRequest wr2 = new WriteRequest().add(series2, dp2);
    assertEquals(wr1, wr2);
  }

  @Test
  public void testNotEquals_Series() {
    WriteRequest wr1 = new WriteRequest().add(series1, dp1);
    WriteRequest wr2 = new WriteRequest().add(series3, dp2);
    assertFalse(wr1.equals(wr2));
  }

  @Test
  public void testNotEquals_DataPoint() {
    WriteRequest wr1 = new WriteRequest().add(series1, dp1);
    WriteRequest wr2 = new WriteRequest().add(series2, dp3);
    assertFalse(wr1.equals(wr2));
  }

  @Test
  public void testNotEquals_Null() {
    WriteRequest wr = new WriteRequest().add(series1, dp1);
    assertFalse(wr.equals(null));
  }
}
