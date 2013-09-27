package com.tempodb;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import com.tempodb.MultiStatus;
import com.tempodb.json.Json;


/**
 *  Response from an API call.
 *
 *  <p>The Result object returns the requested entity as well as provides
 *  information about the success state of the request. The Result state (getState()) should
 *  be inspected before trying to use the value.
 *
 *  <p>In the event of a failure, the value is set to null. The code and message will provide more
 *  information about the failure.
 *
 *  <p>In some cases, a request can partially succeed. This is indicated with state PARTIAL_SUCCESS.
 *  If a partial success occurs, the {@link MultiStatus} (member of Result) will be populated with
 *  more information about which request failed.
 *
 *  @since 1.0.0
 */
public class Result<T> {

  private final T value;
  private final int code;
  private final String message;
  private final MultiStatus multistatus;

  private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

  public Result(T value, int code, String message) {
    this(value, code, message, null);
  }

  /**
   *  Used mainly for testing.
   *  @param value The returned value.
   *  @param code The status code of the entire result.
   *  @param message Message providing information about the state of the result.
   *  @param multistatus Provides information about partially successful result
   *  @since 1.0.0
   */
  public Result(T value, int code, String message, MultiStatus multistatus) {
    this.value = value;
    this.code = code;
    this.message = message;
    this.multistatus = multistatus;
  }

  /**
   *  Base constructor.
   *
   *  Uses the HttpResponse to generate the proper Result.
   *  @param response Http response
   *  @param klass Class of the value
   *  @since 1.0.0
   */
  public Result(HttpResponse response, Class<T> klass) throws IOException {
    T value = null;
    int code = response.getStatusLine().getStatusCode();
    String message = response.getStatusLine().getReasonPhrase();
    MultiStatus multistatus = null;

    switch(getState(code)) {
      case SUCCESS:
        value = newInstanceFromResponse(response, klass);
        break;
      case PARTIAL_SUCCESS:
        multistatus = Json.loads(EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET), MultiStatus.class);
        break;
      case FAILURE:
        message = messageFromResponse(response);
        break;
    }

    this.value = value;
    this.code = code;
    this.message = message;
    this.multistatus = multistatus;
  }

  /**
   *  Returns the value of the Result.
   *  @return Result value. Null if Result state is not SUCCESS.
   *  @since 1.0.0
   */
  public T getValue() { return value; }

  /**
   *  Returns the status code of the Result.
   *  @return Result status code.
   *  @since 1.0.0
   */
  public int getCode() { return code; }

  /**
   *  Returns the message of the Result.
   *  @return Result message.
   *  @since 1.0.0
   */
  public String getMessage() { return message; }

  /**
   *  Returns the MultiStatus of the Result.
   *  @return Result MultiStatus. Null if Result state is not PARTIAL_SUCCESS.
   *  @since 1.0.0
   */
  public MultiStatus getMultiStatus() { return multistatus; }

  /**
   *  Returns the State of the Result.
   *  The state should be used to determine the success state of the Result.
   *  @return Result state
   *  @since 1.0.0
   */
  public State getState() { return getState(code); }

  private static State getState(int code) {
    State state = null;
    if(((code / 100) == 2) && (code != 207)) {
      state = State.SUCCESS;
    } else if(code == 207) {
      state = State.PARTIAL_SUCCESS;
    } else {
      state = State.FAILURE;
    }
    return state;
  }

  private static <T> T newInstanceFromResponse(HttpResponse response, Class<T> klass) {
    Throwable cause = null;
    try {
      Method method = klass.getDeclaredMethod("make", HttpResponse.class);
      return klass.cast(method.invoke(null, response));
    }
    catch (IllegalAccessException e) { cause = e; }
    catch (InvocationTargetException e) { cause = e;  }
    catch (NoSuchMethodException e) { cause = e; }

    throw new IllegalArgumentException("Unknown class: " + klass, cause);
  }

  private String messageFromResponse(HttpResponse response) throws IOException {
    String message = EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);
    if(message == null || message.equals("")) {
      message = response.getStatusLine().getReasonPhrase();
    }
    return message;
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
    if(!(obj instanceof Result<?>)) return false;

    Result<?> rhs = (Result<?>)obj;
    return new EqualsBuilder()
      .append(value, rhs.value)
      .append(code, rhs.code)
      .append(message, rhs.message)
      .append(multistatus, rhs.multistatus)
      .isEquals();
  }
}
