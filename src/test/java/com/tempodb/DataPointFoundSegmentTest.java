package com.tempodb;

import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.junit.*;
import static org.junit.Assert.*;


public class DataPointFoundSegmentTest {

  private static final DateTime start = new DateTime(2012, 1, 1, 0, 0, 0, 0);
  private static final DateTime end1 = new DateTime(2012, 1, 1, 1, 0, 0, 0);
  private static final DateTime end2 = new DateTime(2012, 1, 1, 2, 0, 0, 0);

  private static final Interval interval1 = new Interval(start, end1);
  private static final Interval interval2 = new Interval(start, end1);
  private static final Interval interval3 = new Interval(start, end2);

  private static final DataPoint dp1 = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
  private static final DataPoint dp2 = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
  private static final DataPoint dp3 = new DataPoint(new DateTime(2012, 1, 1, 0, 1, 0, 0), 10.34);

  private List<DataPointFound> data1 = Arrays.asList(
    new DataPointFound(interval1, dp1)
  );
  private List<DataPointFound> data2 = Arrays.asList(
    new DataPointFound(interval2, dp2)
  );
  private List<DataPointFound> data3 = Arrays.asList(
    new DataPointFound(interval3, dp3)
  );

  private Predicate predicate1 = new Predicate(Period.minutes(1), "max");
  private Predicate predicate2 = new Predicate(Period.minutes(1), "max");
  private Predicate predicate3 = new Predicate(Period.minutes(2), "max");

  private DateTimeZone zone1 = DateTimeZone.forID("UTC");
  private DateTimeZone zone2 = DateTimeZone.forID("UTC");
  private DateTimeZone zone3 = DateTimeZone.forID("America/Chicago");

  @Test
  public void testEquals() {
    DataPointFoundSegment segment1 = new DataPointFoundSegment(data1, null, zone1, predicate1);
    DataPointFoundSegment segment2 = new DataPointFoundSegment(data2, null, zone2, predicate2);
    assertEquals(segment1, segment2);
  }

  @Test
  public void testNotEquals_Data() {
    DataPointFoundSegment segment1 = new DataPointFoundSegment(data1, null, zone1, predicate1);
    DataPointFoundSegment segment2 = new DataPointFoundSegment(data3, null, zone2, predicate2);
    assertFalse(segment1.equals(segment2));
  }

  @Test
  public void testNotEquals_TZ() {
    DataPointFoundSegment segment1 = new DataPointFoundSegment(data1, null, zone1, predicate1);
    DataPointFoundSegment segment2 = new DataPointFoundSegment(data2, null, zone3, predicate2);
    assertFalse(segment1.equals(segment2));
  }

  @Test
  public void testNotEquals_Predicate() {
    DataPointFoundSegment segment1 = new DataPointFoundSegment(data1, null, zone1, predicate1);
    DataPointFoundSegment segment2 = new DataPointFoundSegment(data2, null, zone2, predicate3);
    assertFalse(segment1.equals(segment2));
  }

  @Test
  public void testNotEquals_Null() {
    DataPointFoundSegment segment1 = new DataPointFoundSegment(data1, null, zone1, predicate1);
    assertFalse(segment1.equals(null));
  }
}
