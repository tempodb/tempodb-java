package com.tempodb;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static com.tempodb.util.Preconditions.*;


public class DeleteSummary implements Serializable {

  private int deleted;

  /** Serialization lock */
  private static final long serialVersionUID = 1L;

  public DeleteSummary() {
    this(0);
  }

  public DeleteSummary(int deleted) {
    this.deleted = deleted;
  }

  public int getDeleted() { return deleted; }
  public void setDeleted(int deleted) { this.deleted = deleted; }

  @Override
  public String toString() {
    return String.format("DeleteSummary(deleted=%d)", deleted);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(179, 181)
      .append(deleted)
      .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null) return false;
    if(obj == this) return true;
    if(!(obj instanceof DeleteSummary)) return false;

    DeleteSummary rhs = (DeleteSummary)obj;
    return new EqualsBuilder()
      .append(deleted, rhs.deleted)
      .isEquals();
  }
}
