package com.tempodb;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;

import static com.tempodb.util.Preconditions.*;


/**
 *  DataPoint for a Series, used for bulk writing of DataPoints.
 *  <p>Allows you to specify a timestamp/value pair, as well as the {@link Series}
 *  that it is associated with.
 *  @since 1.0.0
 */
public class MultiDataPoint implements Serializable {

  private Series series;
  private DateTime timestamp;
  private Number value;

  /** Serialization lock */
  private static final long serialVersionUID = 1L;

  public MultiDataPoint() {
    this(new Series(""), new DateTime(), 0.0);
  }

  /**
   *  Base constructor
   *  @param series {@link Series} for the DataPoint
   *  @param timestamp Timestamp for the DataPoint
   *  @param value Value for the DataPoint
   *  @since 1.0.0
   */
  public MultiDataPoint(Series series, DateTime timestamp, Number value) {
    this.series = checkNotNull(series);
    this.timestamp = checkNotNull(timestamp);
    this.value = checkNotNull(value);
  }

  /**
   *  Returns the {@link Series} of this MultiDataPoint.
   *  @return the {@link Series}
   *  @since 1.0.0
   */
  public Series getSeries() { return series; }

  /**
   *  Sets the {@link Series} of this MultiDataPoint.
   *  @param series The {@link Series}
   *  @since 1.0.0
   */
  public void setSeries(Series series) { this.series = checkNotNull(series); }

  /**
   *  Returns the timestamp of this MultiDataPoint.
   *  @return the timestamp
   *  @since 1.0.0
   */
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
  public Number getValue() { return value; }

  /**
   *  Sets the value of this MultiDataPoint.
   *  @param value The value of this MultiDataPoint
   *  @since 1.0.0
   */
  public void setValue(Number value) { this.value = checkNotNull(value); }

  @Override
  public String toString() {
    return String.format("MultiDataPoint(series=%s, timestamp=%s, value=%s", series, timestamp, value);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(137, 139)
      .append(series)
      .append(timestamp)
      .append(value)
      .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null) return false;
    if(obj == this) return true;
    if(!(obj instanceof MultiDataPoint)) return false;

    MultiDataPoint rhs = (MultiDataPoint)obj;
    return new EqualsBuilder()
      .append(series, rhs.series)
      .append(timestamp, rhs.timestamp)
      .append(value, rhs.value)
      .isEquals();
  }
}
