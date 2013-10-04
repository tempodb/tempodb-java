package com.tempodb.json;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.*;
import static org.junit.Assert.*;
import org.junit.rules.ExpectedException;

import com.tempodb.MultiDataPoint;
import com.tempodb.Series;


public class MultiDataPointTest {
  private static final DateTimeZone timezone = DateTimeZone.UTC;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testSerialize() throws IOException {
    DateTimeZone zone = DateTimeZone.UTC;
    MultiDataPoint datapoint = new MultiDataPoint(new Series("key1"),new DateTime(2012, 1, 1, 0, 0, 1, 0, timezone), 12.34);

    String expected = "{\"key\":\"key1\",\"t\":\"2012-01-01T00:00:01.000Z\",\"v\":12.34}";
    assertEquals(expected, Json.dumps(datapoint));
  }
}
