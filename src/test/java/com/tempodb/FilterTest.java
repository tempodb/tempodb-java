package com.tempodb;

import java.util.HashMap;
import java.util.HashSet;

import org.junit.*;
import static org.junit.Assert.*;


public class FilterTest {

  @Test
  public void testEquals() {
    Filter f1 = new Filter();
    Filter f2 = new Filter();

    f1.addTag("tag");
    f2.addTag("tag");

    f1.addAttribute("key", "value");
    f2.addAttribute("key", "value");

    assertEquals(f1, f2);
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

  @Test
  public void testAddKeys() {
    Filter f1 = new Filter().addKey("key1").addKey("key2").addKey("key3");
    Filter f2 = new Filter().addKey("key1").addKeys("key2", "key3");
    assertEquals(f1, f2);
  }

  @Test
  public void testAddKeysSet() {
    Filter f1 = new Filter().addKey("key1").addKey("key2").addKey("key3");
    HashSet<String> keys = new HashSet<String>();
    keys.add("key2");
    keys.add("key3");
    Filter f2 = new Filter().addKey("key1").addKeys(keys);
    assertEquals(f1, f2);
  }

  @Test
  public void testAddTags() {
    Filter f1 = new Filter().addTag("tag1").addTag("tag2").addTag("tag3");
    Filter f2 = new Filter().addTag("tag1").addTags("tag2", "tag3");
    assertEquals(f1, f2);
  }

  @Test
  public void testAddTagsSet() {
    Filter f1 = new Filter().addTag("tag1").addTag("tag2").addTag("tag3");
    HashSet<String> tags = new HashSet<String>();
    tags.add("tag2");
    tags.add("tag3");
    Filter f2 = new Filter().addTag("tag1").addTags(tags);
    assertEquals(f1, f2);
  }

  @Test
  public void testAddAttributes() {
    Filter f1 = new Filter().addAttribute("key1", "value1").addAttribute("key2", "value2").addAttribute("key3", "value3");
    HashMap<String, String> attributes = new HashMap<String, String>();
    attributes.put("key2", "value2");
    attributes.put("key3", "value3");
    Filter f2 = new Filter().addAttribute("key1", "value1").addAttributes(attributes);
    assertEquals(f1, f2);
  }
}
