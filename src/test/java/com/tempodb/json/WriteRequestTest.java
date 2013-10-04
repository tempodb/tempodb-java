package com.tempodb.json;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.*;
import static org.junit.Assert.*;
import org.junit.rules.ExpectedException;

import com.tempodb.DataPoint;
import com.tempodb.Series;
import com.tempodb.WriteRequest;


public class WriteRequestTest {
  private static final Series series = new Series("key1");

  @Test
  public void testSerializeUTC() throws IOException {
    DateTimeZone timezone = DateTimeZone.UTC;
    DataPoint dp = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 1, 0, timezone), 12.34);
    WriteRequest wr = new WriteRequest().add(series, dp);

    String expected = "[{\"key\":\"key1\",\"t\":\"2012-01-01T00:00:01.000Z\",\"v\":12.34}]";
    assertEquals(expected, Json.dumps(wr));
  }

  @Test
  public void testSerializeTZ() throws IOException {
    DateTimeZone timezone = DateTimeZone.forID("America/Chicago");
    DataPoint dp = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 1, 0, timezone), 12.34);
    WriteRequest wr = new WriteRequest().add(series, dp);

    String expected = "[{\"key\":\"key1\",\"t\":\"2012-01-01T00:00:01.000-06:00\",\"v\":12.34}]";
    assertEquals(expected, Json.dumps(wr));
  }
}
