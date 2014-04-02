package com.tempodb;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import static com.tempodb.util.Preconditions.*;

/**
 *  A timestamp/value pair.
 *  @since 1.1.0
 */
public class DataPointFound implements Serializable {

  private Interval interval;
  private DataPoint datapoint;

  /** Serialization lock */
  private static final long serialVersionUID = 1L;

  public DataPointFound() {
    this(new Interval(new DateTime(), new DateTime()), new DataPoint(new DateTime(), 0.0));
  }

  /**
   *  Base constructor
   *  @param interval The interval of the found range
   *  @param datapoint The found datapoint (may be null) for some predicates
   *  @since 1.1.0
   */
  public DataPointFound(@JsonProperty("interval") Interval interval, @JsonProperty("found") DataPoint datapoint) {
    this.interval = checkNotNull(interval);
    this.datapoint = checkNotNull(datapoint);
  }

  /**
   *  Returns the interval of this DataPointFound.
   *  @return the interval
   *  @since 1.1.0
   */
  @JsonProperty("interval")
  public Interval getInterval() { return interval; }

  /**
   *  Sets the interval of this DataPointFound.
   *  @param interval The interval of this DataPointFound
   *  @since 1.1.0
   */
  public void setInterval(Interval interval) { this.interval = checkNotNull(interval); }

  /**
   *  Returns the datapoint of this DataPointFound.
   *  @return the datapoint
   *  @since 1.1.0
   */
  @JsonProperty("found")
  public DataPoint getDataPoint() { return datapoint; }

  /**
   *  Sets the datapoint of this DataPointFound.
   *  @param value The datapoint of this DataPointFound
   *  @since 1.1.0
   */
  public void setDataPoint(DataPoint datapoint) { this.datapoint = checkNotNull(datapoint); }

  @Override
  public String toString() {
    return String.format("DataPointFound(interval=%s, datapoint=%s)", interval.toString(), datapoint.toString());
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(19, 31)
      .append(interval)
      .append(datapoint)
      .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null) return false;
    if(obj == this) return true;
    if(!(obj instanceof DataPointFound)) return false;

    DataPointFound rhs = (DataPointFound)obj;
    return new EqualsBuilder()
      .append(interval, rhs.interval)
      .append(datapoint, rhs.datapoint)
      .isEquals();
  }
}
