package com.tempodb;

import java.io.IOException;
import java.nio.charset.Charset;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import com.tempodb.MultiStatus;
import com.tempodb.json.Json;


public class Result<T> {

  private final T value;
  private final int code;
  private final String message;
  private final MultiStatus multistatus;

  private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

  protected Result(T value, int code, String message) {
    this(value, code, message, null);
  }

  protected Result(T value, int code, String message, MultiStatus multistatus) {
    this.value = value;
    this.code = code;
    this.message = message;
    this.multistatus = multistatus;
  }

  protected Result(HttpResponse response, Class<T> klass) throws IOException {
    T value = null;
    int code = response.getStatusLine().getStatusCode();
    String message = response.getStatusLine().getReasonPhrase();
    MultiStatus multistatus = null;

    if(isSuccessful(code)) {
      value = newInstanceFromResponse(response, klass);
    } else {
      if(code == 207) {
        multistatus = Json.loads(EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET), MultiStatus.class);
      } else {
        message = messageFromResponse(response);
      }
    }

    this.value = value;
    this.code = code;
    this.message = message;
    this.multistatus = multistatus;
  }

  private static <T> T newInstanceFromResponse(HttpResponse response, Class<T> klass) throws IOException {
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

  private String messageFromResponse(HttpResponse response) throws IOException {
    String message = EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);
    if(message.equals("") || message == null) {
      message = response.getStatusLine().getReasonPhrase();
    }
    return message;
  }

  public T getValue() { return value; }
  public boolean isSuccessful() { return isSuccessful(code); }
  public int getCode() { return code; }
  public String getMessage() { return message; }
  public MultiStatus getMultiStatus() { return multistatus; }

  private boolean isSuccessful(int code) {
    return ((code / 100) == 2) && (code != 207);
  }

  @Override
  public String toString() {
    return String.format("Result(value=%s, code=%s, message=\"%s\", multistatus=%s)", value, code, message, multistatus);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
      .append(value)
      .append(code)
      .append(message)
      .append(multistatus)
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
      .append(multistatus, rhs.multistatus)
      .isEquals();
  }
}
