package com.tempodb;

import org.joda.time.DateTime;
import org.junit.*;
import static org.junit.Assert.*;


public class MultiDataPointTest {

  @Test
  public void testEquals_Id() {
    MultiDataPoint mdp1 = MultiDataPoint.forId("id", new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
    MultiDataPoint mdp2 = MultiDataPoint.forId("id", new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
    assertEquals(mdp1, mdp2);
  }

  @Test
  public void testEquals_Key() {
    MultiDataPoint mdp1 = MultiDataPoint.forKey("key", new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
    MultiDataPoint mdp2 = MultiDataPoint.forKey("key", new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
    assertEquals(mdp1, mdp2);
  }

  @Test
  public void testNotEquals_Id() {
    MultiDataPoint mdp1 = MultiDataPoint.forId("id1", new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
    MultiDataPoint mdp2 = MultiDataPoint.forId("id2", new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
    assertFalse(mdp1.equals(mdp2));
  }

  @Test
  public void testNotEquals_Key() {
    MultiDataPoint mdp1 = MultiDataPoint.forKey("key1", new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
    MultiDataPoint mdp2 = MultiDataPoint.forKey("key2", new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
    assertFalse(mdp1.equals(mdp2));
  }

  @Test
  public void testNotEquals_Timestamp() {
    MultiDataPoint mdp1 = MultiDataPoint.forId("id", new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
    MultiDataPoint mdp2 = MultiDataPoint.forId("id", new DateTime(2012, 1, 2, 0, 0, 0, 0), 12.34);
    assertFalse(mdp1.equals(mdp2));
  }

  @Test
  public void testNotEquals_Value() {
    MultiDataPoint mdp1 = MultiDataPoint.forId("id", new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
    MultiDataPoint mdp2 = MultiDataPoint.forId("id", new DateTime(2012, 1, 1, 0, 0, 0, 0), 23.45);
    assertFalse(mdp1.equals(mdp2));
  }

  @Test
  public void testNotEquals_Null() {
    MultiDataPoint mdp1 = MultiDataPoint.forId("id", new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
    assertFalse(mdp1.equals(null));
  }
}
