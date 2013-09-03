package com.tempodb;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;


public class DataPoint {

  private DateTime timestamp;
  private Number value;

  public DataPoint(DateTime timestamp, Number value) {
    this.timestamp = timestamp;
    this.value = value;
  }

  public DateTime timestamp() { return timestamp; }
  public Number value() { return value; }

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
