package com.tempodb;

import org.joda.time.Period;
import org.junit.*;
import static org.junit.Assert.*;


public class RollupTest {

  @Test
  public void testEquals() {
    Rollup r1 = new Rollup(Period.minutes(1), Fold.SUM);
    Rollup r2 = new Rollup(Period.minutes(1), Fold.SUM);
    assertEquals(r1, r2);
  }

  @Test
  public void testNotEquals_Period() {
    Rollup r1 = new Rollup(Period.minutes(1), Fold.SUM);
    Rollup r2 = new Rollup(Period.minutes(2), Fold.SUM);
    assertFalse(r1.equals(r2));
  }

  @Test
  public void testNotEquals_Fold() {
    Rollup r1 = new Rollup(Period.minutes(1), Fold.SUM);
    Rollup r2 = new Rollup(Period.minutes(1), Fold.MEAN);
    assertFalse(r1.equals(r2));
  }

  @Test
  public void testNotEquals_Null() {
    Rollup r1 = new Rollup(Period.minutes(1), Fold.SUM);
    assertFalse(r1.equals(null));
  }
}
