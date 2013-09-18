package com.tempodb;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.http.HttpResponse;


public class Nothing {

  public Nothing() { super(); }

  public Nothing(HttpResponse response) { }

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
