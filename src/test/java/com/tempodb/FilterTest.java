package com.tempodb;

import org.junit.*;
import static org.junit.Assert.*;


public class FilterTest {

  @Test
  public void testEquals() {
    Filter f1 = new Filter();
    Filter f2 = new Filter();

    f1.addId("id");
    f2.addId("id");

    f1.addTag("tag");
    f2.addTag("tag");

    f1.addAttribute("key", "value");
    f2.addAttribute("key", "value");

    assertEquals(f1, f2);
  }

  @Test
  public void testNotEquals_Ids() {
    Filter f1 = new Filter();
    Filter f2 = new Filter();
    f1.addId("id1");
    f1.addId("id2");
    assertFalse(f1.equals(f2));
  }

  @Test
  public void testNotEquals_Keys() {
    Filter f1 = new Filter();
    Filter f2 = new Filter();
    f1.addKey("key1");
    f1.addKey("key2");
    assertFalse(f1.equals(f2));
  }

  @Test
  public void testNotEquals_Tags() {
    Filter f1 = new Filter();
    Filter f2 = new Filter();
    f1.addTag("tag1");
    f1.addTag("tag2");
    assertFalse(f1.equals(f2));
  }

  @Test
  public void testNotEquals_Attributes() {
    Filter f1 = new Filter();
    Filter f2 = new Filter();
    f1.addAttribute("key", "value1");
    f1.addAttribute("key", "value1");
    assertFalse(f1.equals(f2));
  }

  @Test
  public void testNotEquals_Null() {
    Filter f1 = new Filter();
    assertFalse(f1.equals(null));
  }
}
