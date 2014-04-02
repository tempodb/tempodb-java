package com.tempodb;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.Period;

import static com.tempodb.util.Preconditions.*;


/**
 *  The representation of a predicate for a find datapoints call
 *
 *  A Predicate allows you to specify a time period and function for a
 *  {@link DataPointFound} query. A Predicate allows you to find datapoints matching
 *  a specific condition. For instance, to find the hourly max (timestamp and value)
 *  the Predicate is specified as:
 *
 *  <p><pre>
 *  import org.joda.time.Period;
 *
 *  Predicate predicate = new Predicate(Period.hours(1), "max");
 *  </pre>
 *  @since 1.1.0
 */
public class Predicate implements Serializable {
  private Period period;
  private String function;

  /** Serialization lock */
  private static final long serialVersionUID = 1L;

  public Predicate() {
    this(Period.minutes(1), "max");
  }

  /**
   *  Base constructor
   *
   *  @param period The rollup period.
   *  @param function The function.
   *  @since 1.1.0
   */
  public Predicate(@JsonProperty("period") Period period, @JsonProperty("function") String function) {
    this.period = checkNotNull(period);
    this.function = checkNotNull(function);
  }

  /**
   *  Returns the predicate period.
   *  @return The predicate period.
   *  @since 1.1.0
   */
  @JsonProperty("period")
  public Period getPeriod() { return period; }

  /**
   *  Sets the predicate period.
   *  @param period The period.
   *  @since 1.1.0
   */
  public void setPeriod(Period period) { this.period = checkNotNull(period); }

  /**
   * Returns the predicate function.
   * @return The predicate folding function.
   * @since 1.1.0
   */
  @JsonProperty("function")
  public String getFunction() { return function; }

  /** Sets the predicate function.
   *  @param function The predicate function.
   *  @since 1.1.0
   */
  public void setFunction(String function) { this.function = checkNotNull(function); }

  @Override
  public String toString() {
    return String.format("Predicate(period=%s, function=\"%s\")", period.toString(), function.toLowerCase());
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
    if(!(obj instanceof Predicate)) return false;

    Predicate rhs = (Predicate)obj;
    return new EqualsBuilder()
      .append(period, rhs.period)
      .append(function, rhs.function)
      .isEquals();
  }
}
