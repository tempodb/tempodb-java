package com.tempodb;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.Period;

import static com.tempodb.util.Preconditions.*;


/**
 *  The representation of a rollup of a single {@link Series}.
 *
 *  A Rollup allows you to specify a time period and folding function for a
 *  {@link DataPoint} query. A Rollup represents a reduction in the amount of data
 *  returned to you. For instance, if you want to know the hourly average of a range
 *  of data, the Rollup is specified as:
 *
 *  <p><pre>
 *  import org.joda.time.Period;
 *
 *  Rollup rollup = new Rollup(Period.hours(1), Fold.SUM);
 *  </pre>
 *  @see Fold
 *  @since 1.0.0
 */
public class Rollup implements Serializable {
  private Period period;
  private Fold fold;

  /** Serialization lock */
  private static final long serialVersionUID = 1L;

  public Rollup() {
    this(Period.minutes(1), Fold.SUM);
  }

  /**
   *  Base constructor
   *
   *  @param period The rollup period.
   *  @param fold The rollup folding function.
   *  @since 1.0.0
   */
  public Rollup(@JsonProperty("period") Period period, @JsonProperty("fold") Fold fold) {
    this.period = checkNotNull(period);
    this.fold = checkNotNull(fold);
  }

  /**
   *  Returns the rollup period.
   *  @return The rollup period.
   *  @since 1.0.0
   */
  @JsonProperty("period")
  public Period getPeriod() { return period; }

  /**
   *  Sets the rollup period.
   *  @param period The period.
   *  @since 1.0.0
   */
  public void setPeriod(Period period) { this.period = checkNotNull(period); }

  /**
   * Returns the rollup folding function.
   * @return The rollup folding function.
   * @since 1.0.0
   */
  @JsonProperty("fold")
  public Fold getFold() { return fold; }

  /** Sets the rollup folding function.
   *  @param fold The rollup folding function.
   *  @since 1.0.0
   */
  public void setFold(Fold fold) { this.fold = checkNotNull(fold); }

  @Override
  public String toString() {
    return String.format("Rollup(period=%s,fold=%s)", period.toString(), fold.toString().toLowerCase());
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(13, 41)
      .append(period)
      .append(fold)
      .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null) return false;
    if(obj == this) return true;
    if(!(obj instanceof Rollup)) return false;

    Rollup rhs = (Rollup)obj;
    return new EqualsBuilder()
      .append(period, rhs.period)
      .append(fold, rhs.fold)
      .isEquals();
  }
}
