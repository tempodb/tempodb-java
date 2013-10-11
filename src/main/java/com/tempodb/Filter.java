package com.tempodb;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


/**
 *  Represents a filter on the set of Series. This is used to query a set of series with specific
 *  properties. A filter can include keys, tags and attributes. An empty filter is created
 *  and filter predicates are added. For example a filter to return series with keys myagley-1 and
 *  myagley-2 looks like this:
 *
 *  <p><pre>
 *  Filter filter = new Filter();
 *  filter.addKey("myagley-1");
 *  filter.addKey("myagley-1");
 *  </pre>
 *  @since 1.0.0
 */
public class Filter {
  private final Set<String> keys = new LinkedHashSet<String>();
  private final Set<String> tags = new LinkedHashSet<String>();
  private final Map<String, String> attributes = new HashMap<String, String>();

  /**
   *  Returns the keys to filter on.
   *  @return A set of keys.
   *  @since 1.0.0
   */
  public Set<String> getKeys() { return keys; }

  /**
   *  Returns the tags to filter on.
   *  @return A set of tags.
   *  @since 1.0.0
   */
  public Set<String> getTags() { return tags; }

  /**
   *  Returns the attributes to filter on.
   *  @return A map of attributes.
   *  @since 1.0.0
   */
  public Map<String, String> getAttributes() { return attributes; }

  /**
   *  Adds a key to the filter.
   *
   *  @param key The key to add.
   *  @return This filter
   *  @since 1.0.0
   */
  public synchronized Filter addKey(String key) {
    keys.add(key);
    return this;
  }

  /**
   *  Adds a list of keys to the filter.
   *
   *  @param keys The keys to add.
   *  @return This filter
   *  @since 1.0.0
   */
  public synchronized Filter addKeys(String... keys) {
    for(String key : keys) {
      this.keys.add(key);
    }
    return this;
  }

  /**
   *  Adds a set of keys to the filter.
   *
   *  @param keys The set of keys to add.
   *  @return This filter
   *  @since 1.0.0
   */
  public synchronized Filter addKeys(Set<String> keys) {
    for(String key : keys) {
      this.keys.add(key);
    }
    return this;
  }

  /**
   *  Adds a tag to the filter.
   *
   *  @param tag The tag to add.
   *  @return This filter
   *  @since 1.0.0
   */
  public synchronized Filter addTag(String tag) {
    tags.add(tag);
    return this;
  }

  /**
   *  Adds a list of tags to the filter.
   *
   *  @param tags The tags to add.
   *  @return This filter
   *  @since 1.0.0
   */
  public synchronized Filter addTags(String... tags) {
    for(String tag : tags) {
      this.tags.add(tag);
    }
    return this;
  }

  /**
   *  Adds a set of tags to the filter.
   *
   *  @param tags The set of tags to add.
   *  @return This filter
   *  @since 1.0.0
   */
  public synchronized Filter addTags(Set<String> tags) {
    for(String tag : tags) {
      this.tags.add(tag);
    }
    return this;
  }

  /**
   *  Adds an attribute to the filter.
   *
   *  @param key The attribute key.
   *  @param value The attribute value.
   *  @return This filter
   *  @since 1.0.0
   */
  public synchronized Filter addAttribute(String key, String value) {
    attributes.put(key, value);
    return this;
  }

  /**
   *  Adds a map of attributes to the filter.
   *
   *  @param attributes The map of attributes to add.
   *  @return This filter
   *  @since 1.0.0
   */
  public synchronized Filter addAttributes(Map<String, String> attributes) {
    for(Map.Entry<String, String> pair : attributes.entrySet()) {
      String key = pair.getKey();
      String value = pair.getValue();
      this.attributes.put(key, value);
    }
    return this;
  }

  @Override
  public String toString() {
    return String.format("Filter(keys=%s, tags=%s, attributes=%s", keys, tags, attributes);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(113, 117)
      .append(keys)
      .append(tags)
      .append(attributes)
      .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null) return false;
    if(obj == this) return true;
    if(!(obj instanceof Filter)) return false;

    Filter rhs = (Filter)obj;
    return new EqualsBuilder()
      .append(keys, rhs.keys)
      .append(tags, rhs.tags)
      .append(attributes, rhs.attributes)
      .isEquals();
  }
}
