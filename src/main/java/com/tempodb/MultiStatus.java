package com.tempodb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static com.tempodb.util.Preconditions.*;


public class MultiStatus implements Iterable<Status>, Serializable {

  private List<Status> statuses;

  /** Serialization lock */
  private static final long serialVersionUID = 1L;

  public MultiStatus() {
    this(new ArrayList<Status>());
  }

  public MultiStatus(List<Status> statuses) {
    this.statuses = checkNotNull(statuses);
  }

  public List<Status> getStatuses() { return statuses; }
  public void setStatuses(List<Status> statuses) { this.statuses = checkNotNull(statuses); }

  public Iterator<Status> iterator() {
    return statuses.iterator();
  }

  @Override
  public String toString() {
    return String.format("MultiStatus(statuses=%s)", statuses);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(167, 173)
      .append(statuses)
      .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null) return false;
    if(obj == this) return true;
    if(!(obj instanceof MultiStatus)) return false;

    MultiStatus rhs = (MultiStatus)obj;
    return new EqualsBuilder()
      .append(statuses, rhs.statuses)
      .isEquals();
  }
}
