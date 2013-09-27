package com.tempodb.unit;

import org.junit.*;
import static org.junit.Assert.*;

import com.tempodb.Aggregation;
import com.tempodb.Fold;


public class AggregationTest {

  @Test
  public void testEquals() {
    Aggregation a1 = new Aggregation(Fold.SUM);
    Aggregation a2 = new Aggregation(Fold.SUM);
    assertEquals(a1, a2);
  }

  @Test
  public void testNotEquals_Fold() {
    Aggregation a1 = new Aggregation(Fold.SUM);
    Aggregation a2 = new Aggregation(Fold.MEAN);
    assertFalse(a1.equals(a2));
  }

  @Test
  public void testNotEquals_Null() {
    Aggregation a1 = new Aggregation(Fold.SUM);
    assertFalse(a1.equals(null));
  }
}
