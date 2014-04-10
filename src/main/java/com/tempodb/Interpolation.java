package com.tempodb;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.Period;

import static com.tempodb.util.Preconditions.*;


/**
 *  The representation of an interpolation.
 *
 *  A Interpolation allows you to specify a time period and function for a
 *  {@link DataPoint} query. An Interpolation will compute values for datapoints at
 *  regular intervals. This is useful for filling in missing data or aligning timestamps
 *  to a period.
 *  <p>Timestamps in the interpolated series are calculated as:
 *  <p><pre>ts_n = interval.start + interpolation.period * n;</pre>
 *  @see InterpolationFunction
 *  @since 1.1.0
 */
public class Interpolation implements Serializable {
  private Period period;
  private InterpolationFunction function;

  /** Serialization lock */
  private static final long serialVersionUID = 1L;

  public Interpolation() {
    this(Period.minutes(1), InterpolationFunction.LINEAR);
  }

  /**
   *  Base constructor
   *
   *  @param period The interpolation period.
   *  @param function The interpolation function.
   *  @since 1.1.0
   */
  public Interpolation(@JsonProperty("period") Period period, @JsonProperty("function") InterpolationFunction function) {
    this.period = checkNotNull(period);
    this.function = checkNotNull(function);
  }

  /**
   *  Returns the interpolation period.
   *  @return The interpolation period.
   *  @since 1.1.0
   */
  @JsonProperty("period")
  public Period getPeriod() { return period; }

  /**
   *  Sets the interpolation period.
   *  @param period The period.
   *  @since 1.1.0
   */
  public void setPeriod(Period period) { this.period = checkNotNull(period); }

  /**
   * Returns the interpolation function.
   * @return The interpolation function.
   * @since 1.1.0
   */
  @JsonProperty("function")
  public InterpolationFunction getFunction() { return function; }

  /** Sets the interpolation function.
   *  @param function The interpolation function.
   *  @since 1.1.0
   */
  public void setFunction(InterpolationFunction function) { this.function = checkNotNull(function); }

  /** Creates a zero-order-hold interpolation instance
   *  @param period The interpolation period
   *  @since 1.1.0
   */
  public static Interpolation zoh(Period period)
  {
    return new Interpolation(period, InterpolationFunction.ZOH);
  }

  /** Creates a linear interpolation instance
   *  @param period The interpolation period
   *  @since 1.1.0
   */
  public static Interpolation linear(Period period)
  {
    return new Interpolation(period, InterpolationFunction.LINEAR);
  }

  @Override
  public String toString() {
    return String.format("Interpolation(period=%s,function=%s)", period.toString(), function.toString().toLowerCase());
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(13, 41)
      .append(period)
      .append(function)
      .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null) return false;
    if(obj == this) return true;
    if(!(obj instanceof Interpolation)) return false;

    Interpolation rhs = (Interpolation)obj;
    return new EqualsBuilder()
      .append(period, rhs.period)
      .append(function, rhs.function)
      .isEquals();
  }
}
