package com.tempodb;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;

import static com.tempodb.util.Preconditions.*;


public class MultiDataPoint {
  private final String id;
  private final String key;
  private final DateTime timestamp;
  private final Number value;

  private MultiDataPoint(String id, String key, DateTime timestamp, Number value) {
    checkArgument(id != null || key != null, "Id or key must be non-null - id: %s, key: %s", id, key);
    this.id = id;
    this.key = key;
    this.timestamp = checkNotNull(timestamp);
    this.value = checkNotNull(value);
  }

  public static MultiDataPoint forId(String id, DateTime timestamp, Number value) {
    return new MultiDataPoint(id, null, timestamp, value);
  }

  public static MultiDataPoint forKey(String key, DateTime timestamp, Number value) {
    return new MultiDataPoint(null, key, timestamp, value);
  }

  public String getId() { return id; }
  public String getKey() { return key; }
  public DateTime getTimestamp() { return timestamp; }
  public Number getValue() { return value; }

  @Override
  public String toString() {
    return String.format("MultiDataPoint(id=%s, key=%s, timestamp=%s, value=%s", id, key, timestamp, value);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(137, 139)
      .append(id)
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
      .append(id, rhs.id)
      .append(key, rhs.key)
      .append(timestamp, rhs.timestamp)
      .append(value, rhs.value)
      .isEquals();
  }
}
