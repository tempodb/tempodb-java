package com.tempodb.it;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.junit.*;
import static org.junit.Assert.*;

import com.tempodb.*;


public class ClientIT {
  private Client client = new Client("dc6943b5ec9c48df9f027ffd4a5c9a43", "a7b88b6707cf481baefeb9de1f47479f", "api-staging.tempo-db.com", 443, true);
  private DateTime start = new DateTime(1500, 1, 1, 0, 0, 0, 0);
  private DateTime end = new DateTime(3000, 1, 1, 0, 0, 0, 0);
  private DateTimeZone timezone = DateTimeZone.UTC;

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
    /* Delete all datapoints all series */
    Cursor<Series> cursor = client.getSeriesByFilter(new Filter());
    for(Series series : cursor) {
      Result<Nothing> result = client.deleteDataPointsByKey(series.getKey(), new Interval(start, end));
    }

    /* Delete all series */
    Result<DeleteSummary> result = client.deleteAllSeries();
  }

  @Test
  public void testWriteDataPointKey() {
    DataPoint dp = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0), 12.34);
    Result<Nothing> result = client.writeDataPointsByKey("key1", Arrays.asList(dp));
    assertEquals(State.SUCCESS, result.getState());
  }

  @Ignore
  @Test
  public void testReadDataPointKey() throws InterruptedException {
    DataPoint dp1 = new DataPoint(new DateTime(2012, 1, 2, 0, 0 ,0, 0, timezone), 23.45);
    DataPoint dp2 = new DataPoint(new DateTime(2012, 1, 2, 1, 0 ,0, 0, timezone), 34.56);

    Result<Nothing> result = client.writeDataPointsByKey("key1", Arrays.asList(dp1, dp2));
    Thread.sleep(2);

    DateTime start = new DateTime(2012, 1, 2, 0, 0, 0, 0, timezone);
    DateTime end = new DateTime(2012, 1, 3, 0, 0, 0, 0, timezone);

    List<DataPoint> expected = Arrays.asList(dp1, dp2);

    Cursor<DataPoint> cursor = client.readDataPointsByKey("key1", new Interval(start, end), null, timezone);
    List<DataPoint> output = new ArrayList<DataPoint>();
    for(DataPoint dp : cursor) {
      output.add(dp);
    }
    assertEquals(expected, output);
  }
}
