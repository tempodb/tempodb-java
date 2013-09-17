package com.tempodb;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


/**
 *  Represents a filter on the set of Series. This is used to query a set of series with specific
 *  properties. A filter can include ids, keys, tags and attributes. An empty filter is created
 *  and filter predicates are added. For example a filter to return series with keys myagley-1 and
 *  myagley-2 looks like this:
 *  <pre>
 *  {@code
 *  Filter filter = new Filter();
 *  filter.addKey("myagley-1");
 *  filter.addKey("myagley-1");
 *  }
 *  </pre>
 */
public class Filter {
  private final Set<String> ids = new LinkedHashSet<String>();
  private final Set<String> keys = new LinkedHashSet<String>();
  private final Set<String> tags = new LinkedHashSet<String>();
  private final Map<String, String> attributes = new HashMap<String, String>();

  public Set<String> getIds() { return ids; }
  public Set<String> getKeys() { return keys; }
  public Set<String> getTags() { return tags; }
  public Map<String, String> getAttributes() { return attributes; }

  /**
   *  Adds an id to the filter.
   *
   *  @param id The id to add
   */
  public synchronized void addId(String id) { ids.add(id); }

  /**
   *  Adds a key to the filter.
   *
   *  @param key The key to add
   */
  public synchronized void addKey(String key) { keys.add(key); }

  /**
   *  Adds a tag to the filter.
   *
   *  @param tag The tag to add
   */
  public synchronized void addTag(String tag) { tags.add(tag); }

  /**
   *  Adds an attribute to the filter.
   *
   *  @param key The attribute key
   *  @param value The attribute value
   */
  public synchronized void addAttribute(String key, String value) { attributes.put(key, value); }

  @Override
  public String toString() {
    return String.format("Filter(ids=%s, keys=%s, tags=%s, attributes=%s", ids, keys, tags, attributes);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(113, 117)
      .append(ids)
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
      .append(ids, rhs.ids)
      .append(keys, rhs.keys)
      .append(tags, rhs.tags)
      .append(attributes, rhs.attributes)
      .isEquals();
  }
}
