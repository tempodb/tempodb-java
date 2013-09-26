package com.tempodb;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.http.HttpResponse;


/**
 *  Represents nothing returned from an API call.
 *
 *  <p>This is required for the type parameter of the {@link Result}.
 *  @since 1.0.0
 */
public class Nothing {
  private static final Nothing nothing = new Nothing();

  public Nothing() { super(); }

  static Nothing make(HttpResponse response) { return nothing; }

  @Override
  public String toString() {
    return "Nothing()";
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(149, 151).toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null) return false;
    if(obj == null) return true;
    if(!(obj instanceof Nothing)) return false;
    return true;
  }
}
