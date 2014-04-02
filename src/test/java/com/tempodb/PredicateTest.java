package com.tempodb;

import org.joda.time.Period;
import org.junit.*;
import static org.junit.Assert.*;


public class PredicateTest {

  @Test
  public void testEquals() {
    Predicate r1 = new Predicate(Period.minutes(1), "max");
    Predicate r2 = new Predicate(Period.minutes(1), "max");
    assertEquals(r1, r2);
  }

  @Test
  public void testNotEquals_Period() {
    Predicate r1 = new Predicate(Period.minutes(1), "max");
    Predicate r2 = new Predicate(Period.minutes(2), "max");
    assertFalse(r1.equals(r2));
  }

  @Test
  public void testNotEquals_Fold() {
    Predicate r1 = new Predicate(Period.minutes(1), "max");
    Predicate r2 = new Predicate(Period.minutes(1), "min");
    assertFalse(r1.equals(r2));
  }

  @Test
  public void testNotEquals_Null() {
    Predicate r1 = new Predicate(Period.minutes(1), "max");
    assertFalse(r1.equals(null));
  }
}
