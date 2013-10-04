package com.tempodb;

import org.junit.*;
import static org.junit.Assert.*;


public class DatabaseTest {

  @Test
  public void testEquals() {
    Database d1 = new Database("id");
    Database d2 = new Database("id");
    assertEquals(d1, d2);
  }

  @Test
  public void testNotEquals_Id() {
    Database d1 = new Database("id1");
    Database d2 = new Database("id2");
    assertFalse(d1.equals(d2));
  }

  @Test
  public void testNotEquals_Null() {
    Database d1 = new Database("id");
    assertFalse(d1.equals(null));
  }
}
