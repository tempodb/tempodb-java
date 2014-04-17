package com.tempodb;

import java.io.Serializable;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.Period;

import static com.tempodb.util.Preconditions.*;


/**
 *  The representation of a rollup with multiple folding function of a single {@link Series}.
 *
 *  A MultiRollup allows you to specify a time period and an an array of folding functions for a
 *  {@link DataPoint} query. A MultiRollup represents a reduction in the amount of data
 *  returned to you. For instance, if you want to know the hourly average of a range
 *  of data, the MultiRollup is specified as:
 *
 *  <p><pre>
 *  import org.joda.time.Period;
 *
 *  MultiRollup rollup = new MultiRollup(Period.hours(1), new Fold[] { Fold.SUM, Fold.MAX });
 *  </pre>
 *  @see Fold
 *  @since 1.1.0
 */
public class MultiRollup implements Serializable {
  private Period period;
  private Fold[] folds;

  /** Serialization lock */
  private static final long serialVersionUID = 1L;

  public MultiRollup() {
    this(Period.minutes(1), new Fold[] { Fold.SUM });
  }

  /**
   *  Base constructor
   *
   *  @param period The rollup period.
   *  @param folds The rollup folding functions.
   *  @since 1.1.0
   */
  public MultiRollup(@JsonProperty("period") Period period, @JsonProperty("folds") Fold[] folds) {
    this.period = checkNotNull(period);
    this.folds = checkNotNull(folds);
  }

  /**
   *  Returns the rollup period.
   *  @return The rollup period.
   *  @since 1.1.0
   */
  @JsonProperty("period")
  public Period getPeriod() { return period; }

  /**
   *  Sets the rollup period.
   *  @param period The period.
   *  @since 1.1.0
   */
  public void setPeriod(Period period) { this.period = checkNotNull(period); }

  /**
   * Returns the rollup folding functions.
   * @return The rollup folding functions.
   * @since 1.1.0
   */
  @JsonProperty("folds")
  public Fold[] getFolds() { return Arrays.copyOf(folds, folds.length); }

  /** Sets the rollup folding functions.
   *  @param folds The rollup folding functions.
   *  @since 1.1.0
   */
  public void setFold(Fold[] folds) { this.folds = checkNotNull(folds); }

  @Override
  public String toString() {
    return String.format("MultiRollup(period=%s,folds=%s)", period.toString(), Arrays.toString(folds).toLowerCase());
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(13, 41)
      .append(period)
      .append(folds)
      .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null) return false;
    if(obj == this) return true;
    if(!(obj instanceof MultiRollup)) return false;

    MultiRollup rhs = (MultiRollup)obj;
    return new EqualsBuilder()
      .append(period, rhs.period)
      .append(folds, rhs.folds)
      .isEquals();
  }
}
