package com.tempodb;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class Result<T> {

  private final T value;
  private final int code;
  private final String message;

  protected Result(T value, int code, String message) {
    this.value = value;
    this.code = code;
    this.message = message;
  }

  public T getValue() { return value; }
  public boolean isSuccessful() { return (code / 100) == 2; }
  public int getCode() { return code; }
  public String getMessage() { return message; }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
      .append(value)
      .append(code)
      .append(message)
      .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null) return false;
    if(obj == this) return true;
    if(!(obj instanceof Result)) return false;

    Result<T> rhs = (Result<T>)obj;
    return new EqualsBuilder()
      .append(value, rhs.value)
      .append(code, rhs.code)
      .append(message, rhs.message)
      .isEquals();
  }
}
