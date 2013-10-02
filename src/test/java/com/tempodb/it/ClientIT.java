package com.tempodb.it;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.junit.*;
import static org.junit.Assert.*;

import com.tempodb.*;
import static com.tempodb.util.Preconditions.*;


public class ClientIT {
  private static final Client client;
  private static final Client invalidClient;
  private static final DateTimeZone timezone = DateTimeZone.UTC;
  private static final DateTime start = new DateTime(1500, 1, 1, 0, 0, 0, 0, timezone);
  private static final DateTime end = new DateTime(3000, 1, 1, 0, 0, 0, 0, timezone);

  static {
    File credentials = new File("integration-credentials.properties");
    if(!credentials.exists()) {
      String message = "Missing credentials file for integration test.\n" +
        "Please supply a file 'integration-credentials.properties' with the following format:\n" +
        "  database.id=<id>\n" +
        "  credentials.key=<key>\n" +
        "  credentials.secret=<secret>\n" +
        "  hostname=<hostname>\n" +
        "  port=<port>\n" +
        "  secure=<secure>\n";

      System.out.println(message);
      System.exit(1);
    }

    client = getClient(credentials);
    invalidClient = new Client("key", "secret", client.getHost(), client.getPort(), client.getSecure());
  }

  static Client getClient(File propertiesFile) {
    Properties properties = new Properties();
    try {
      properties.load(new FileInputStream(propertiesFile));
    } catch (Exception e) {
      throw new IllegalArgumentException("No credentials file", e);
    }

    String id = checkNotNull(properties.getProperty("database.id"));
    String key = checkNotNull(properties.getProperty("credentials.key"));
    String secret = checkNotNull(properties.getProperty("credentials.secret"));
    String hostname = checkNotNull(properties.getProperty("hostname"));
    int port = Integer.parseInt(checkNotNull(properties.getProperty("port")));
    boolean secure = Boolean.parseBoolean(checkNotNull(properties.getProperty("secure")));

    return new Client(key, secret, hostname, port, secure);
  }

  @BeforeClass
  static public void onetimeSetup() {
    cleanup();
  }

  static public void cleanup() {
    /* Delete all datapoints all series */
    Cursor<Series> cursor = client.getSeriesByFilter(new Filter());
    for(Series series : cursor) {
      Result<Nothing> result = client.deleteDataPointsByKey(series.getKey(), new Interval(start, end));
      assertEquals(State.SUCCESS, result.getState());
    }

    /* Delete all series */
    Result<DeleteSummary> result = client.deleteAllSeries();
    assertEquals(State.SUCCESS, result.getState());
  }

  @After
  public void tearDown() { cleanup(); }

  @Test
  public void testInvalidCredentials() {
    Series series = new Series("key1", "", new HashSet<String>(), new HashMap<String, String>());
    Result<Series> result = invalidClient.createSeries(series);
    Result<Series> expected = new Result<Series>(null, 403, "Forbidden");
    assertEquals(expected, result);
  }

  @Test
  public void testCreateSeries() {
    HashSet<String> tags = new HashSet<String>();
    tags.add("create");

    Series series = new Series("create-series", "name", tags, new HashMap<String, String>());

    Result<Series> result = client.createSeries(series);
    Result<Series> expected = new Result<Series>(series, 200, "OK");

    assertEquals(expected, result);
  }

  @Test
  public void testDeleteDataPointsByKey() throws InterruptedException {
    Interval interval = new Interval(start, end);

    // Write datapoints
    DataPoint dp = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0, timezone), 12.34);
    Result<Nothing> result1 = client.writeDataPointsByKey("key1", Arrays.asList(dp));
    assertEquals(State.SUCCESS, result1.getState());
    Thread.sleep(2);

    // Read datapoints
    List<DataPoint> expected1 = Arrays.asList(dp);
    Cursor<DataPoint> cursor1 = client.readDataPointsByKey("key1", interval, null, timezone);
    assertEquals(expected1, toList(cursor1));

    // Delete datapoints
    Result<Nothing> result2 = client.deleteDataPointsByKey("key1", interval);
    assertEquals(new Result(new Nothing(), 200, "OK"), result2);

    // Read datapoints again
    List<DataPoint> expected2 = new ArrayList<DataPoint>();
    Cursor<DataPoint> cursor2 = client.readDataPointsByKey("key1", interval, null, timezone);
    assertEquals(expected2, toList(cursor2));
  }

  @Test
  public void testWriteDataPointKey() {
    DataPoint dp = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0, timezone), 12.34);
    Result<Nothing> result = client.writeDataPointsByKey("key1", Arrays.asList(dp));
    assertEquals(State.SUCCESS, result.getState());
  }

  @Test
  public void testReadDataPointKey() throws InterruptedException {
    DataPoint dp1 = new DataPoint(new DateTime(2012, 1, 2, 0, 0 ,0, 0, timezone), 23.45);
    DataPoint dp2 = new DataPoint(new DateTime(2012, 1, 2, 1, 0 ,0, 0, timezone), 34.56);

    Result<Nothing> result = client.writeDataPointsByKey("key1", Arrays.asList(dp1, dp2));
    assertEquals(State.SUCCESS, result.getState());
    Thread.sleep(2);

    DateTime start = new DateTime(2012, 1, 2, 0, 0, 0, 0, timezone);
    DateTime end = new DateTime(2012, 1, 3, 0, 0, 0, 0, timezone);

    List<DataPoint> expected = Arrays.asList(dp1, dp2);
    Cursor<DataPoint> cursor = client.readDataPointsByKey("key1", new Interval(start, end), null, timezone);
    assertEquals(expected, toList(cursor));
  }

  private List<DataPoint> toList(Cursor<DataPoint> cursor) {
    List<DataPoint> output = new ArrayList<DataPoint>();
    for(DataPoint dp : cursor) {
      output.add(dp);
    }
    return output;
  }
}
