package com.tempodb.models;

import org.joda.time.DateTime;

import org.junit.Test;
import static org.junit.Assert.assertEquals;


public class DataPointTest {

  @Test
  public void testConstructor() {
    DataPoint dp = new DataPoint(new DateTime(2012, 3, 27, 1, 0, 0, 0), 12.34);

    assertEquals(dp.getTimestamp(), new DateTime(2012, 3, 27, 1, 0, 0, 0));
    assertEquals(dp.getValue(), 12.34);
  }
}
