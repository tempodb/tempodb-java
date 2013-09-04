package com.tempodb;

import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.junit.*;
import static org.junit.Assert.*;

import com.tempodb.DataPoint;
import com.tempodb.Fold;
import com.tempodb.Rollup;


public class DataPointSegmentTest {

  private List<DataPoint> data1 = Arrays.asList(new DataPoint(new DateTime(2012, 1, 1, 0, 0, 1, 0, DateTimeZone.UTC), 12.34));
  private List<DataPoint> data2 = Arrays.asList(new DataPoint(new DateTime(2012, 1, 1, 0, 0, 1, 0, DateTimeZone.UTC), 12.34));
  private List<DataPoint> data3 = Arrays.asList(new DataPoint(new DateTime(2012, 1, 2, 0, 0, 1, 0, DateTimeZone.UTC), 12.34));

  private Rollup rollup1 = new Rollup(Period.minutes(1), Fold.SUM);
  private Rollup rollup2 = new Rollup(Period.minutes(1), Fold.SUM);
  private Rollup rollup3 = new Rollup(Period.minutes(2), Fold.SUM);

  private DateTimeZone zone1 = DateTimeZone.forID("UTC");
  private DateTimeZone zone2 = DateTimeZone.forID("UTC");
  private DateTimeZone zone3 = DateTimeZone.forID("America/Chicago");

  @Test
  public void testEquals() {
    DataPointSegment segment1 = new DataPointSegment(data1, null, zone1, rollup1);
    DataPointSegment segment2 = new DataPointSegment(data2, null, zone2, rollup2);
    assertEquals(segment1, segment2);
  }

  @Test
  public void testNotEquals_Data() {
    DataPointSegment segment1 = new DataPointSegment(data1, null, zone1, rollup1);
    DataPointSegment segment2 = new DataPointSegment(data3, null, zone2, rollup2);
    assertFalse(segment1.equals(segment2));
  }

  @Test
  public void testNotEquals_TZ() {
    DataPointSegment segment1 = new DataPointSegment(data1, null, zone1, rollup1);
    DataPointSegment segment2 = new DataPointSegment(data2, null, zone3, rollup2);
    assertFalse(segment1.equals(segment2));
  }

  @Test
  public void testNotEquals_Rollup() {
    DataPointSegment segment1 = new DataPointSegment(data1, null, zone1, rollup1);
    DataPointSegment segment2 = new DataPointSegment(data2, null, zone2, rollup3);
    assertFalse(segment1.equals(segment2));
  }

  @Test
  public void testNotEquals_Null() {
    DataPointSegment segment1 = new DataPointSegment(data1, null, zone1, rollup1);
    assertFalse(segment1.equals(null));
  }
}
