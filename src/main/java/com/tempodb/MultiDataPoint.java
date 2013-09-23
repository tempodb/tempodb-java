package com.tempodb;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;

import static com.tempodb.util.Preconditions.*;


public class MultiDataPoint implements Serializable {

  private String key;
  private DateTime timestamp;
  private Number value;

  /** Serialization lock */
  private static final long serialVersionUID = 1L;

  public MultiDataPoint() {
    this("", new DateTime(), 0.0);
  }

  public MultiDataPoint(@JsonProperty("key") String key, @JsonProperty("t") DateTime timestamp, @JsonProperty("v") Number value) {
    this.key = checkNotNull(key);
    this.timestamp = checkNotNull(timestamp);
    this.value = checkNotNull(value);
  }

  @JsonProperty("key")
  public String getKey() { return key; }
  public void setKey(String key) { this.key = checkNotNull(key); }

  @JsonProperty("t")
  public DateTime getTimestamp() { return timestamp; }
  public void setTimestamp(DateTime timestamp) { this.timestamp = checkNotNull(timestamp); }

  @JsonProperty("v")
  public Number getValue() { return value; }
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
