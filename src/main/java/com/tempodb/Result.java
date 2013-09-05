package com.tempodb;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;


public class Result<T> {

  private final T value;
  private final int code;
  private final String message;

  protected Result(T value, int code, String message) {
    this.value = value;
    this.code = code;
    this.message = message;
  }

  protected Result(HttpResponse response, Class<T> klass) throws IOException {
    int code = response.getStatusLine().getStatusCode();
    this.code = code;
    if(isSuccessful(code)) {
      this.value = newInstanceFromResponse(response, klass);
      this.message = "";
    } else {
      this.value = null;
      this.message = EntityUtils.toString(response.getEntity());
    }
  }

  protected static <T> T newInstanceFromResponse(HttpResponse response, Class<T> klass) throws IOException {
    Throwable cause = null;
    try {
      return klass.getConstructor(HttpResponse.class).newInstance(response);
    }
    catch (InstantiationException e) { cause = e; }
    catch (IllegalAccessException e) { cause = e; }
    catch (InvocationTargetException e) { cause = e;  }
    catch (NoSuchMethodException e) { cause = e; }

    throw new IllegalArgumentException("Unknown class: " + klass, cause);
  }

  public T getValue() { return value; }
  public boolean isSuccessful() { return isSuccessful(code); }
  public int getCode() { return code; }
  public String getMessage() { return message; }

  private boolean isSuccessful(int code) {
    return (code / 100) == 2;
  }

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
