package com.tempodb;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;

import static com.tempodb.util.Preconditions.*;

/**
 *  A Series/DataPoint pair for use with the getSingleValue calls
 *  @since 1.1.0
 */
public class SingleValue implements Serializable {

  private Series series;
  private DataPoint datapoint;

  /** Serialization lock */
  private static final long serialVersionUID = 1L;

  public SingleValue() {
    this(new Series(), new DataPoint());
  }

  /**
   *  Base constructor
   *  @param series The Series associated with the datapoint
   *  @param datapoint The datapoint
   *  @since 1.1.0
   */
  public SingleValue(@JsonProperty("series") Series series, @JsonProperty("data") DataPoint datapoint) {
    this.series = checkNotNull(series);
    this.datapoint = checkNotNull(datapoint);
  }

  /**
   *  Returns the series of this SingleValue.
   *  @return the series
   *  @since 1.1.0
   */
  @JsonProperty("series")
  public Series getSeries() { return series; }

  /**
   *  Sets the series of this SingleValue.
   *  @param series The series of this SingleValue
   *  @since 1.1.0
   */
  public void setSeries(Series series) { this.series = checkNotNull(series); }

  /**
   *  Returns the datapoint of this SingleValue.
   *  @return the datapoint
   *  @since 1.1.0
   */
  @JsonProperty("data")
  public DataPoint getDataPoint() { return datapoint; }

  /**
   *  Sets the datapoint of this SingleValue.
   *  @param datapoint The DataPoint of this SingleValue
   *  @since 1.1.0
   */
  public void setDataPoint(DataPoint datapoint) { this.datapoint = checkNotNull(datapoint); }

  @Override
  public String toString() {
    return String.format("SingleValue(series=%s, datapoint=%s)", series.toString(), datapoint.toString());
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(19, 31)
      .append(series)
      .append(datapoint)
      .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null) return false;
    if(obj == this) return true;
    if(!(obj instanceof SingleValue)) return false;

    SingleValue rhs = (SingleValue)obj;
    return new EqualsBuilder()
      .append(series, rhs.series)
      .append(datapoint, rhs.datapoint)
      .isEquals();
  }
}
