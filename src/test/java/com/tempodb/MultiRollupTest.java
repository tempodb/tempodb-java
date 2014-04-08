package com.tempodb;

import org.joda.time.Period;
import org.junit.*;
import static org.junit.Assert.*;


public class MultiRollupTest {

  private static Period p1 = Period.minutes(1);
  private static Period p2 = Period.minutes(1);
  private static Period p3 = Period.minutes(2);

  private static Fold[] f1 = new Fold[] { Fold.MEAN, Fold.SUM };
  private static Fold[] f2 = new Fold[] { Fold.MEAN, Fold.SUM };
  private static Fold[] f3 = new Fold[] { Fold.SUM };

  @Test
  public void testEquals() {
    MultiRollup mr1 = new MultiRollup(p1, f1);
    MultiRollup mr2 = new MultiRollup(p2, f2);
    assertEquals(mr1, mr2);
  }

  @Test
  public void testNotEquals_Period() {
    MultiRollup mr1 = new MultiRollup(p1, f1);
    MultiRollup mr2 = new MultiRollup(p3, f2);
    assertFalse(mr1.equals(mr2));
  }

  @Test
  public void testNotEquals_Fold() {
    MultiRollup mr1 = new MultiRollup(p1, f1);
    MultiRollup mr2 = new MultiRollup(p2, f3);
    assertFalse(mr1.equals(mr2));
  }

  @Test
  public void testNotEquals_Null() {
    MultiRollup mr1 = new MultiRollup(p1, f1);
    assertFalse(mr1.equals(null));
  }
}
