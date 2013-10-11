package com.tempodb;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
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

import static com.tempodb.util.Preconditions.*;


public class ClientIT {
  private static final Client client;
  private static final Client invalidClient;
  private static final DateTimeZone timezone = DateTimeZone.UTC;
  private static final DateTime start = new DateTime(1500, 1, 1, 0, 0, 0, 0, timezone);
  private static final DateTime end = new DateTime(3000, 1, 1, 0, 0, 0, 0, timezone);
  private static final Interval interval = new Interval(start, end);

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
        "  scheme=<scheme>\n";

      System.out.println(message);
      System.exit(1);
    }

    client = getClient(credentials);
    invalidClient = new Client(client.getDatabase(), new Credentials("key", "secret"),
                               client.getAddress(), client.getScheme(), client.getPort());
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
    String scheme = checkNotNull(properties.getProperty("scheme"));

    Database database = new Database(id);
    Credentials credentials = new Credentials(key, secret);
    InetAddress address = null;
    try {
      address = InetAddress.getByName(hostname);
    } catch (UnknownHostException e) { }

    return new Client(database, credentials, address, scheme, port);
  }

  @BeforeClass
  static public void onetimeSetup() {
    cleanup();
  }

  static public void cleanup() {
    /* Delete all datapoints all series */
    Cursor<Series> cursor = client.getSeries(new Filter());
    for(Series series : cursor) {
      Result<Nothing> result = client.deleteDataPoints(series, interval);
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
  public void testDeleteDataPointsBySeries() throws InterruptedException {
    // Write datapoints
    DataPoint dp = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0, timezone), 12.34);
    Result<Nothing> result1 = client.writeDataPoints(new Series("key1"), Arrays.asList(dp));
    assertEquals(State.SUCCESS, result1.getState());
    Thread.sleep(2000);

    // Read datapoints
    List<DataPoint> expected1 = Arrays.asList(dp);
    Cursor<DataPoint> cursor1 = client.readDataPoints(new Series("key1"), interval, null, timezone);
    assertEquals(expected1, toList(cursor1));

    // Delete datapoints
    Result<Nothing> result2 = client.deleteDataPoints(new Series("key1"), interval);
    assertEquals(new Result<Nothing>(new Nothing(), 200, "OK"), result2);

    // Read datapoints again
    List<DataPoint> expected2 = new ArrayList<DataPoint>();
    Cursor<DataPoint> cursor2 = client.readDataPoints(new Series("key1"), interval, null, timezone);
    assertEquals(expected2, toList(cursor2));
  }

  @Test
  public void testWriteDataPointBySeries() {
    DataPoint dp = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0, timezone), 12.34);
    Result<Nothing> result = client.writeDataPoints(new Series("key1"), Arrays.asList(dp));
    assertEquals(State.SUCCESS, result.getState());
  }

  @Test
  public void testReadDataPointByKey() throws InterruptedException {
    DataPoint dp1 = new DataPoint(new DateTime(2012, 1, 2, 0, 0 ,0, 0, timezone), 23.45);
    DataPoint dp2 = new DataPoint(new DateTime(2012, 1, 2, 1, 0 ,0, 0, timezone), 34.56);

    Result<Nothing> result = client.writeDataPoints(new Series("key1"), Arrays.asList(dp1, dp2));
    assertEquals(State.SUCCESS, result.getState());
    Thread.sleep(2000);

    DateTime start = new DateTime(2012, 1, 2, 0, 0, 0, 0, timezone);
    DateTime end = new DateTime(2012, 1, 3, 0, 0, 0, 0, timezone);

    List<DataPoint> expected = Arrays.asList(dp1, dp2);
    Cursor<DataPoint> cursor = client.readDataPoints(new Series("key1"), new Interval(start, end), null, timezone);
    assertEquals(expected, toList(cursor));
  }

  @Test
  public void testWriteDataPoints() throws InterruptedException {
    WriteRequest wr = new WriteRequest()
      .add(new Series("key1"), new DataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0, timezone), 5.0))
      .add(new Series("key2"), new DataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0, timezone), 6.0))
      .add(new Series("key1"), new DataPoint(new DateTime(2012, 1, 1, 0, 1, 0, 0, timezone), 7.0))
      .add(new Series("key2"), new DataPoint(new DateTime(2012, 1, 1, 0, 2, 0, 0, timezone), 8.0));

    Thread.sleep(2000);

    Result<Nothing> result = client.writeDataPoints(wr);
    assertEquals(new Result<Nothing>(new Nothing(), 200, "OK"), result);
  }

  @Test
  public void testReadDataPoints() throws InterruptedException {
    WriteRequest wr = new WriteRequest()
      .add(new Series("key1"), new DataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0, timezone), 5.0))
      .add(new Series("key2"), new DataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0, timezone), 6.0))
      .add(new Series("key1"), new DataPoint(new DateTime(2012, 1, 1, 0, 1, 0, 0, timezone), 7.0))
      .add(new Series("key2"), new DataPoint(new DateTime(2012, 1, 1, 0, 1, 0, 0, timezone), 8.0));

    Thread.sleep(2000);

    Result<Nothing> result1 = client.writeDataPoints(wr);
    assertEquals(new Result<Nothing>(new Nothing(), 200, "OK"), result1);

    Filter filter = new Filter();
    filter.addKey("key1");
    filter.addKey("key2");
    Aggregation aggregation = new Aggregation(Fold.SUM);
    Cursor<DataPoint> cursor = client.readDataPoints(filter, interval, aggregation, null, timezone);

    DataPoint dp1 = new DataPoint(new DateTime(2012, 1, 1, 0, 0, 0, 0, timezone), 11.0);
    DataPoint dp2 = new DataPoint(new DateTime(2012, 1, 1, 0, 1, 0, 0, timezone), 15.0);
    List<DataPoint> expected = Arrays.asList(dp1, dp2);
    assertEquals(expected, toList(cursor));
  }

  @Test
  public void testGetSeriesByKey() {
    // Create a series
    HashSet<String> tags = new HashSet<String>();
    tags.add("create");
    Series series = new Series("create-series", "name", tags, new HashMap<String, String>());
    Result<Series> result1 = client.createSeries(series);

    // Get the series
    Result<Series> result2 = client.getSeries("create-series");
    Result<Series> expected = new Result<Series>(series, 200, "OK");
    assertEquals(expected, result2);
  }

  @Test
  public void testGetSeriesByFilter() {
    // Create a series
    HashSet<String> tags = new HashSet<String>();
    tags.add("get-filter");
    Series series = new Series("create-series", "name", tags, new HashMap<String, String>());
    Result<Series> result1 = client.createSeries(series);

    // Get the series by filter
    Filter filter = new Filter();
    filter.addTag("get-filter");
    Cursor<Series> cursor = client.getSeries(filter);
    List<Series> expected = Arrays.asList(series);
    assertEquals(expected, toList(cursor));
  }

  @Test
  public void testReplaceSeriesByKey() {
    // Create a series
    HashSet<String> tags = new HashSet<String>();
    tags.add("replace");
    Series series = new Series("replace-series", "name", tags, new HashMap<String, String>());
    Result<Series> result1 = client.createSeries(series);

    // Replace the series
    series.getTags().add("replace2");
    Result<Series> result2 = client.replaceSeries(series);
    assertEquals(new Result<Series>(series, 200, "OK"), result2);

    // Get the series
    Result<Series> result3 = client.getSeries("replace-series");
    Result<Series> expected = new Result<Series>(series, 200, "OK");
    assertEquals(expected, result3);
  }

  @Test
  public void testDeleteSeries() {
    // Create a series
    HashSet<String> tags = new HashSet<String>();
    tags.add("delete");
    Series series = new Series("delete-series", "name", tags, new HashMap<String, String>());
    Result<Series> result1 = client.createSeries(series);

    // Delete the series
    Result<Nothing> result2 = client.deleteSeries(series);
    assertEquals(new Result<Nothing>(new Nothing(), 200, "OK"), result2);

    // Get the series
    Result<Series> result3 = client.getSeries("delete-series");
    Result<Series> expected = new Result<Series>(null, 403, "Forbidden");
    assertEquals(expected, result3);
  }

  @Test
  public void testDeleteSeriesByFilter() {
    // Create a series
    HashSet<String> tags = new HashSet<String>();
    tags.add("delete-filter");
    Series series1 = new Series("delete-series", "name", tags, new HashMap<String, String>());
    Series series2 = new Series("delete-series2", "name", new HashSet<String>(), new HashMap<String, String>());
    Result<Series> result1 = client.createSeries(series1);
    Result<Series> result2 = client.createSeries(series2);

    // Get the series by filter
    Filter filter = new Filter();
    filter.addTag("delete-filter");
    Cursor<Series> cursor = client.getSeries(filter);
    List<Series> expected1 = Arrays.asList(series1);
    assertEquals(expected1, toList(cursor));

    // Delete the series by filter
    Result<DeleteSummary> result3 = client.deleteSeries(filter);
    assertEquals(new Result<DeleteSummary>(new DeleteSummary(1), 200, "OK"), result3);

    // Get the series by filter again
    Cursor<Series> cursor2 = client.getSeries(filter);
    List<Series> expected2 = Arrays.asList();
    assertEquals(expected2, toList(cursor2));
  }

  private <T> List<T> toList(Cursor<T> cursor) {
    List<T> output = new ArrayList<T>();
    for(T dp : cursor) {
      output.add(dp);
    }
    return output;
  }
}
