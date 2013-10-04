package com.tempodb;

import org.junit.*;
import static org.junit.Assert.*;


public class DeleteSummaryTest {

  @Test
  public void testEquals() {
    DeleteSummary d1 = new DeleteSummary(0);
    DeleteSummary d2 = new DeleteSummary(0);
    assertEquals(d1, d2);
  }

  @Test
  public void testNotEquals() {
    DeleteSummary d1 = new DeleteSummary(0);
    DeleteSummary d2 = new DeleteSummary(2);
    assertFalse(d1.equals(d2));
  }

  @Test
  public void testNotEquals_Null() {
    DeleteSummary d1 = new DeleteSummary(0);
    assertFalse(d1.equals(null));
  }
}
