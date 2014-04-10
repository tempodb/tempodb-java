package com.tempodb;

import org.joda.time.Period;
import org.junit.*;
import static org.junit.Assert.*;


public class InterpolationTest {

  @Test
  public void testEquals() {
    Interpolation r1 = new Interpolation(Period.minutes(1), InterpolationFunction.LINEAR);
    Interpolation r2 = new Interpolation(Period.minutes(1), InterpolationFunction.LINEAR);
    assertEquals(r1, r2);
  }

  @Test
  public void testNotEquals_Period() {
    Interpolation r1 = new Interpolation(Period.minutes(1), InterpolationFunction.LINEAR);
    Interpolation r2 = new Interpolation(Period.minutes(2), InterpolationFunction.LINEAR);
    assertFalse(r1.equals(r2));
  }

  @Test
  public void testNotEquals_InterpolationFunction() {
    Interpolation r1 = new Interpolation(Period.minutes(1), InterpolationFunction.LINEAR);
    Interpolation r2 = new Interpolation(Period.minutes(1), InterpolationFunction.ZOH);
    assertFalse(r1.equals(r2));
  }

  @Test
  public void testNotEquals_Null() {
    Interpolation r1 = new Interpolation(Period.minutes(1), InterpolationFunction.LINEAR);
    assertFalse(r1.equals(null));
  }
}
