package com.tempodb.json;

import java.io.IOException;

import org.joda.time.Period;
import org.junit.*;
import static org.junit.Assert.*;

import com.tempodb.Fold;
import com.tempodb.MultiRollup;


public class MultiRollupTest {

  @Test
  public void testDeserialize() throws IOException {
    String json = "{" +
      "\"period\":\"PT1M\"," +
      "\"folds\":[\"max\",\"sum\"]" +
    "}";

    MultiRollup rollup = Json.loads(json, MultiRollup.class);
    MultiRollup expected = new MultiRollup(Period.minutes(1), new Fold[] { Fold.MAX, Fold.SUM });
    assertEquals(expected, rollup);
  }

  @Test
  public void testSerialize() throws IOException {
    MultiRollup rollup = new MultiRollup(Period.minutes(1), new Fold[] { Fold.MAX, Fold.SUM });
    String expected = "{" +
      "\"period\":\"PT1M\"," +
      "\"folds\":[\"max\",\"sum\"]" +
    "}";
    assertEquals(expected, Json.dumps(rollup));
  }
}
