package com.tempodb;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;

import static com.tempodb.util.Preconditions.*;


/**
 *  DataPoint for a Series, used for bulk writing of DataPoints.
 *  <p>Allows you to specify a timestamp/value pair, as well as the {@link Series} key
 *  that it is associated with.
 *  @since 1.0.0
 */
public class MultiDataPoint implements Serializable {

  private String key;
  private DateTime timestamp;
  private Number value;

  /** Serialization lock */
  private static final long serialVersionUID = 1L;

  public MultiDataPoint() {
    this("", new DateTime(), 0.0);
  }

  /**
   *  Base constructor
   *  @param key {@link Series} key for the DataPoint
   *  @param timestamp Timestamp for the DataPoint
   *  @param value Value for the DataPoint
   *  @since 1.0.0
   */
  public MultiDataPoint(@JsonProperty("key") String key, @JsonProperty("t") DateTime timestamp, @JsonProperty("v") Number value) {
    this.key = checkNotNull(key);
    this.timestamp = checkNotNull(timestamp);
    this.value = checkNotNull(value);
  }

  /**
   *  Returns the {@link Series} key of this MultiDataPoint.
   *  @return the {@link Series} key
   *  @since 1.0.0
   */
  @JsonProperty("key")
  public String getKey() { return key; }

  /**
   *  Sets the {@link Series} key of this MultiDataPoint.
   *  @param key The {@link Series} key
   *  @since 1.0.0
   */
  public void setKey(String key) { this.key = checkNotNull(key); }

  /**
   *  Returns the timestamp of this MultiDataPoint.
   *  @return the timestamp
   *  @since 1.0.0
   */
  @JsonProperty("t")
  public DateTime getTimestamp() { return timestamp; }

  /**
   *  Sets the timestamp of this MultiDataPoint.
   *  @param timestamp The timestamp of this MultiDataPoint
   *  @since 1.0.0
   */
  public void setTimestamp(DateTime timestamp) { this.timestamp = checkNotNull(timestamp); }

  /**
   *  Returns the value of this MultiDataPoint.
   *  @return the value
   *  @since 1.0.0
   */
  @JsonProperty("v")
  public Number getValue() { return value; }

  /**
   *  Sets the value of this MultiDataPoint.
   *  @param value The value of this MultiDataPoint
   *  @since 1.0.0
   */
  public void setValue(Number value) { this.value = checkNotNull(value); }

  @Override
  public String toString() {
    return String.format("MultiDataPoint(key=%s, timestamp=%s, value=%s", key, timestamp, value);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(137, 139)
      .append(key)
      .append(timestamp)
      .append(value)
      .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null) return false;
    if(obj == null) return true;
    if(!(obj instanceof MultiDataPoint)) return false;

    MultiDataPoint rhs = (MultiDataPoint)obj;
    return new EqualsBuilder()
      .append(key, rhs.key)
      .append(timestamp, rhs.timestamp)
      .append(value, rhs.value)
      .isEquals();
  }
}
