package com.tempodb.json;

import java.io.IOException;

import org.joda.time.Period;
import org.junit.*;
import static org.junit.Assert.*;

import com.tempodb.Predicate;


public class PredicateTest {

  @Test
  public void testDeserialize() throws IOException {
    Predicate rollup = Json.loads("{\"period\":\"PT1M\",\"function\":\"max\"}", Predicate.class);
    Predicate expected = new Predicate(Period.minutes(1), "max");
    assertEquals(expected, rollup);
  }

  @Test
  public void testSerialize() throws IOException {
    Predicate rollup = new Predicate(Period.minutes(1), "max");
    String expected = "{\"period\":\"PT1M\",\"function\":\"max\"}";
    assertEquals(expected, Json.dumps(rollup));
  }
}
