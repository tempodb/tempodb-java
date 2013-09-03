package com.tempodb;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.Period;


public class Rollup {
  private final Period period;
  private final Fold fold;

  public Rollup(@JsonProperty("period") Period period, @JsonProperty("fold") Fold fold) {
    this.period = period;
    this.fold = fold;
  }

  @JsonProperty("period")
  public Period period() { return period; }

  @JsonProperty("fold")
  public Fold fold() { return fold; }

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
