package com.tempodb;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import com.tempodb.json.Json;
import static com.tempodb.util.Preconditions.*;


/**
 *  A data series and it's metadata.
 *
 *  @since 1.0.0
 */
public class Series implements Serializable {

  private String key;
  private String name;
  private Set<String> tags;
  private Map<String, String> attributes;

  /** Serialization lock */
  private static final long serialVersionUID = 1L;

  private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

  public Series() {
    this("", "", new LinkedHashSet<String>(), new HashMap<String, String>());
  }

  /**
   *  Key-only constructor.
   *  <ul>
   *    <li><tt>name</tt> default to ""</li>
   *    <li><tt>tags</tt> default to empty set</li>
   *    <li><tt>attributes</tt> default to empty map</li>
   *  </ul>
   *  @param key Series key
   *  @since 1.0.0
   */
  public Series(String key) {
    this(key, "", new LinkedHashSet<String>(), new HashMap<String, String>());
  }

  /**
   *  Base constructor
   *
   *  @param key Series key
   *  @param name Human readable name for the Series
   *  @param tags Set of tags
   *  @param attributes Map of key/value pair metadata
   *  @since 1.0.0
   */
  public Series(String key, String name, Set<String> tags, Map<String, String> attributes) {
    this.key = checkNotNull(key);
    this.name = checkNotNull(name);
    this.tags = checkNotNull(tags);
    this.attributes = checkNotNull(attributes);
  }

  /**
   *  Returns the key of this Series.
   *  @return Series key.
   *  @since 1.0.0
   */
  public String getKey() { return key; }

  /**
   *  Sets the key of this Series.
   *  @param key Series key.
   *  @since 1.0.0
   */
  public void setKey(String key) { this.key = checkNotNull(key); }

  /**
   *  Returns the name of this Series.
   *  @return Series name.
   *  @since 1.0.0
   */
  public String getName() { return name; }

  /**
   *  Sets the name of this Series.
   *  @param name Series name.
   *  @since 1.0.0
   */
  public void setName(String name) { this.name = checkNotNull(name); }


  /**
   *  Returns the tags of this Series.
   *  @return Series tags.
   *  @since 1.0.0
   */
  public Set<String> getTags() { return tags; }

  /**
   *  Sets the tags of this Series.
   *  @param tags Series tags.
   *  @since 1.0.0
   */
  public void setTags(Set<String> tags) { this.tags = checkNotNull(tags); }

  /**
   *  Returns the attributes of this Series.
   *  @return Series attributes.
   *  @since 1.0.0
   */
  public Map<String, String> getAttributes() { return attributes; }

  /**
   *  Sets the attributes of this Series.
   *  @param attributes Series attributes.
   *  @since 1.0.0
   */
  public void setAttributes(Map<String, String> attributes) { this.attributes = checkNotNull(attributes); }

  static Series make(HttpResponse response) throws IOException {
    String body = EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);
    Series series = Json.loads(body, Series.class);
    return series;
  }

  @Override
  public String toString() {
    return String.format("Series(key=%s, name=%s, tags=%s, attributes=%s)", key, name, tags, attributes);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(119, 123)
      .append(key)
      .append(name)
      .append(tags)
      .append(attributes)
      .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null) return false;
    if(obj == this) return true;
    if(!(obj instanceof Series)) return false;

    Series rhs = (Series)obj;
    return new EqualsBuilder()
      .append(key, rhs.key)
      .append(name, rhs.name)
      .append(tags, rhs.tags)
      .append(attributes, rhs.attributes)
      .isEquals();
  }
}
