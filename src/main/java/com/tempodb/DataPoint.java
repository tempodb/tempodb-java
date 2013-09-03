package com.tempodb;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;

import static com.tempodb.util.Preconditions.*;


public class DataPoint implements Serializable {

  private DateTime timestamp;
  private Number value;

  /** Serialization lock */
  private static final long serialVersionUID = 0L;

  public DataPoint() {
    this(new DateTime(), 0.0);
  }

  public DataPoint(@JsonProperty("t") DateTime timestamp, @JsonProperty("v") Number value) {
    this.timestamp = checkNotNull(timestamp);
    this.value = checkNotNull(value);
  }

  @JsonProperty("t")
  public DateTime getTimestamp() { return timestamp; }
  public void setTimestamp(DateTime timestamp) { this.timestamp = checkNotNull(timestamp); }

  @JsonProperty("v")
  public Number getValue() { return value; }
  public void setValue(Number value) { this.value = checkNotNull(value); }

  @Override
  public String toString() {
    return String.format("DataPoint(timestamp=%s, value=%s)", timestamp.toString(), value.toString());
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(19, 31)
      .append(timestamp)
      .append(value)
      .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null) return false;
    if(obj == this) return true;
    if(!(obj instanceof DataPoint)) return false;

    DataPoint rhs = (DataPoint)obj;
    return new EqualsBuilder()
      .append(timestamp, rhs.timestamp)
      .append(value, rhs.value)
      .isEquals();
  }
}
