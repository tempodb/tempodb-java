package com.tempodb.unit;

import java.util.Arrays;
import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

import com.tempodb.MultiStatus;
import com.tempodb.Status;


public class MultiStatusTest {

  private static final List<Status> statuses1 = Arrays.asList(new Status(1, Arrays.asList("message")));
  private static final List<Status> statuses2 = Arrays.asList(new Status(1, Arrays.asList("message")));
  private static final List<Status> statuses3 = Arrays.asList(new Status(100, Arrays.asList("message1")));

  @Test
  public void testEquals() {
    MultiStatus m1 = new MultiStatus(statuses1);
    MultiStatus m2 = new MultiStatus(statuses2);
    assertEquals(m1, m2);
  }

  @Test
  public void testNotEquals() {
    MultiStatus m1 = new MultiStatus(statuses1);
    MultiStatus m2 = new MultiStatus(statuses3);
    assertFalse(m1.equals(m2));
  }

  @Test
  public void testNotEquals_Null() {
    MultiStatus m1 = new MultiStatus(statuses1);
    assertFalse(m1.equals(null));
  }
}
