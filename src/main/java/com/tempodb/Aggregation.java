package com.tempodb;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static com.tempodb.util.Preconditions.*;


public class Aggregation implements Serializable {
  private Fold fold;

  /** Serialization lock */
  private static final long serialVersionUID = 1L;

  public Aggregation() {
    this(Fold.SUM);
  }

  public Aggregation(Fold fold) {
    this.fold = checkNotNull(fold);
  }

  public Fold getFold() { return fold; }
  public void setFold(Fold fold) { this.fold = checkNotNull(fold); }

  @Override
  public String toString() {
    return String.format("Aggregation(fold=%s)", fold.toString().toLowerCase());
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(107, 109)
      .append(fold)
      .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null) return false;
    if(obj == this) return true;
    if(!(obj instanceof Aggregation)) return false;

    Aggregation rhs = (Aggregation)obj;
    return new EqualsBuilder()
      .append(fold, rhs.fold)
      .isEquals();
  }
}
