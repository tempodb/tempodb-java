package com.tempodb;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;

import static com.tempodb.util.Preconditions.*;

/**
 *  A timestamp/multi-value pair.
 *  Represents a timestamp with multi-values. This is used for cursoring
 *  over multiple series at once or querying multiple rollups for a series.
 *  @since 1.1.0
 */
public class MultiDataPoint implements Serializable {

  private DateTime timestamp;
  private Map<String, Number> data;

  /** Serialization lock */
  private static final long serialVersionUID = 1L;

  public MultiDataPoint() {
    this(new DateTime(), new HashMap<String, Number>());
  }

  /**
   *  Base constructor
   *  @param timestamp The timestamp of the datapoint
   *  @param data Data for this timestamp
   *  @since 1.1.0
   */
  public MultiDataPoint(@JsonProperty("t") DateTime timestamp, @JsonProperty("v") Map<String, Number> data) {
    this.timestamp = checkNotNull(timestamp);
    this.data = checkNotNull(data);
  }

  /**
   *  Returns the timestamp of this DataPoint.
   *  @return the timestamp
   *  @since 1.1.0
   */
  @JsonProperty("t")
  public DateTime getTimestamp() { return timestamp; }

  /**
   *  Sets the timestamp of this datapoint.
   *  @param timestamp The timestamp of this DataPoint
   *  @since 1.1.0
   */
  public void setTimestamp(DateTime timestamp) { this.timestamp = checkNotNull(timestamp); }

  /**
   *  Returns the value of this DataPoint.
   *  @return the value
   *  @since 1.1.0
   */
  @JsonProperty("v")
  public Map<String, Number> getData() { return data; }

  /**
   *  Sets the data of this DataPoint.
   *  @param data The map of data for this DataPoint
   *  @since 1.1.0
   */
  public void setData(Map<String, Number> data) { this.data = checkNotNull(data); }

  /**
   *  Gets the number for this key (series key or rollup fold).
   *  @param key The key to get
   *  @return the value
   *  @since 1.1.0
   */
  public Number get(String key) { return this.data.get(key); }

  @Override
  public String toString() {
    return String.format("MultiDataPoint(timestamp=%s, data=%s)", timestamp.toString(), data.toString());
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(19, 31)
      .append(timestamp)
      .append(data)
      .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null) return false;
    if(obj == this) return true;
    if(!(obj instanceof MultiDataPoint)) return false;

    MultiDataPoint rhs = (MultiDataPoint)obj;
    return new EqualsBuilder()
      .append(timestamp, rhs.timestamp)
      .append(data, rhs.data)
      .isEquals();
  }
}
