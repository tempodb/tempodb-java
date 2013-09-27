package com.tempodb.unit;

import java.util.Arrays;

import org.junit.*;
import static org.junit.Assert.*;

import com.tempodb.Status;


public class StatusTest {

  @Test
  public void testEquals() {
    Status s1 = new Status(1, Arrays.asList("message"));
    Status s2 = new Status(1, Arrays.asList("message"));
    assertEquals(s1, s2);
  }

  @Test
  public void testNotEquals_Code() {
    Status s1 = new Status(1, Arrays.asList("message"));
    Status s2 = new Status(2, Arrays.asList("message"));
    assertFalse(s1.equals(s2));
  }

  @Test
  public void testNotEquals_Messages() {
    Status s1 = new Status(1, Arrays.asList("message1"));
    Status s2 = new Status(1, Arrays.asList("message2"));
    assertFalse(s1.equals(s2));
  }

  @Test
  public void testNotEquals_Null() {
    Status s1 = new Status(1, Arrays.asList("message"));
    assertFalse(s1.equals(null));
  }
}
