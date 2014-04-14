package com.tempodb;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;

import com.tempodb.json.Json;
import static com.tempodb.util.Preconditions.*;


/**
 *  A set of summary statistics for a series.
 *
 *  @since 1.1.0
 */
public class Summary implements Map<String, Number>, Serializable {

  private Series series;
  private Interval interval;
  private Map<String, Number> summary;

  /** Serialization lock */
  private static final long serialVersionUID = 1L;

  private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

  public Summary() {
    this(new Series(""), new Interval(new DateTime(), new DateTime()), new HashMap<String, Number>());
  }

  /**
   *  Base constructor
   *
   *  @param series Series
   *  @param start Start datetime for the summary
   *  @param end End datetime for the summary
   *  @param summary Map of statistic string to value
   *  @since 1.1.0
   */
  public Summary(Series series, Interval interval, Map<String, Number> summary) {
    this.series = checkNotNull(series);
    this.interval = checkNotNull(interval);
    this.summary = checkNotNull(summary);
  }

  /**
   *  Returns the series of this Summary
   *  @return Summary series.
   *  @since 1.1.0
   */
  public Series getSeries() { return series; }

  /**
   *  Sets the series of this Summary.
   *  @param key Summary key.
   *  @since 1.1.0
   */
  public void setSeries(Series series) { this.series = checkNotNull(series); }

  /**
   *  Returns the interval of this Summary.
   *  @return Summary interval.
   *  @since 1.1.0
   */
  public Interval getInterval() { return interval; }

  /**
   *  Sets the interval of this Summary.
   *  @param interval Summary interval.
   *  @since 1.1.0
   */
  public void setInterval(Interval interval) { this.interval = checkNotNull(interval); }

  public void clear() { summary.clear(); }

  public boolean containsKey(Object key) { return summary.containsKey(key); }

  public boolean containsValue(Object value) { return summary.containsValue(value); }

  public Set<Map.Entry<String, Number>> entrySet() { return summary.entrySet(); }

  public Number get(Object key) { return summary.get(key); }

  public boolean isEmpty() { return summary.isEmpty(); }

  public Set<String> keySet() { return summary.keySet(); }

  public Number put(String key, Number value) { return summary.put(key, value); }

  public void putAll(Map<? extends String,? extends Number> m) { summary.putAll(m); }

  public Number remove(Object key) { return summary.remove(key); }

  public int size() { return summary.size(); }

  public Collection<Number> values() { return summary.values(); }

  static Summary make(HttpResponse response) throws IOException {
    String body = EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);
    Summary summary = Json.loads(body, Summary.class);
    return summary;
  }

  @Override
  public String toString() {
    return String.format("Summary(series=%s, interval=%s, summary=%s)", series, interval, summary);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(119, 123)
      .append(series)
      .append(interval)
      .append(summary)
      .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null) return false;
    if(obj == this) return true;
    if(!(obj instanceof Summary)) return false;

    Summary rhs = (Summary)obj;
    return new EqualsBuilder()
      .append(series, rhs.series)
      .append(interval, rhs.interval)
      .append(summary, rhs.summary)
      .isEquals();
  }
}
