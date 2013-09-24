package com.tempodb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static com.tempodb.util.Preconditions.*;


public class Status implements Serializable {

  private int code;
  private List<String> messages;

  /** Serialization lock */
  private static final long serialVersionUID = 1L;

  public Status() {
    this(0, new ArrayList());
  }

  public Status(@JsonProperty("status") int code, @JsonProperty("messages") List<String> messages) {
    this.code = checkNotNull(code);
    this.messages = checkNotNull(messages);
  }

  @JsonProperty("status")
  public int getCode() { return code; }
  public void setCode(int code) { this.code = checkNotNull(code); }

  @JsonProperty("messages")
  public List<String> getMessages() { return messages; }
  public void setMessages(List<String> messages) { this.messages = checkNotNull(messages); }

  @Override
  public String toString() {
    return String.format("Status(code=%d,messages=%s)", code, messages);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(157, 163)
      .append(code)
      .append(messages)
      .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null) return false;
    if(obj == this) return true;
    if(!(obj instanceof Status)) return false;

    Status rhs = (Status)obj;
    return new EqualsBuilder()
      .append(code, rhs.code)
      .append(messages, rhs.messages)
      .isEquals();
  }
}
