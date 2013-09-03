package com.tempodb;

import org.junit.*;
import static org.junit.Assert.*;


public class ResultTest {

  @Test
  public void testConstructor() {
    Result<Long> result = new Result(1L, 200, "message");
    assertEquals(200, result.getCode());
    assertEquals("message", result.getMessage());
  }

  @Test
  public void testEquals() {
    Result<Long> result = new Result(1L, 200, "message");
    Result<Long> expected = new Result(1L, 200, "message");
    assertEquals(expected, result);
  }

  @Test
  public void testNotEquals() {
    Result<Long> result1 = new Result(1L, 200, "message");
    Result<Long> result2 = new Result(2L, 100, "message");
    Result<Long> result3 = new Result(2L, 200, "message1");
    Result<Long> expected = new Result(2L, 200, "message");
    assertFalse(expected.equals(result1));
    assertFalse(expected.equals(result2));
    assertFalse(expected.equals(result3));
  }
}
