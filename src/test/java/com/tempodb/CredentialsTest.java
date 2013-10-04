package com.tempodb;

import org.junit.*;
import static org.junit.Assert.*;


public class CredentialsTest {

  @Test
  public void testEquals() {
    Credentials c1 = new Credentials("key", "secret");
    Credentials c2 = new Credentials("key", "secret");
    assertEquals(c1, c2);
  }

  @Test
  public void testNotEquals_Key() {
    Credentials c1 = new Credentials("key1", "secret");
    Credentials c2 = new Credentials("key2", "secret");
    assertFalse(c1.equals(c2));
  }

  @Test
  public void testNotEquals_Secret() {
    Credentials c1 = new Credentials("key", "secret1");
    Credentials c2 = new Credentials("key", "secret2");
    assertFalse(c1.equals(c2));
  }

  @Test
  public void testNotEquals_Null() {
    Credentials c1 = new Credentials("key", "secret");
    assertFalse(c1.equals(null));
  }
}
