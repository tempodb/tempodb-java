package com.tempodb;

public class TempoDBException extends RuntimeException {
  private int code = 0;

  public TempoDBException() { super(); }

  public TempoDBException(String message, int code) {
    super(message);
    this.code = code;
  }

  public TempoDBException(String message, Throwable cause, int code) {
    super(message, cause);
    this.code = code;
  }
}
