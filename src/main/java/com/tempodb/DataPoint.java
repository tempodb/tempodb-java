package com.tempodb;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;

import static com.tempodb.Preconditions.*;

public class DataPoint {

  private DateTime timestamp = null;
  private Number value = null;

  public DataPoint() { }

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
