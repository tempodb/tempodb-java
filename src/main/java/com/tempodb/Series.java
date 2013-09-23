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


public class Series implements Serializable {

  private String id;
  private String key;
  private String name;
  private Set<String> tags;
  private Map<String, String> attributes;

  /** Serialization lock */
  private static final long serialVersionUID = 1L;

  private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

  public Series() {
    this("", "", "", new LinkedHashSet(), new HashMap());
  }

  public Series(String key, String name, Set<String> tags, Map<String, String> attributes) {
    this("", key, name, tags, attributes);
  }

  public Series(String id, String key, String name, Set<String> tags, Map<String, String> attributes) {
    this.id = checkNotNull(id);
    this.key = checkNotNull(key);
    this.name = checkNotNull(name);
    this.tags = checkNotNull(tags);
    this.attributes = checkNotNull(attributes);
  }

  public Series(HttpResponse response) throws IOException {
    String body = EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);
    Series series = Json.loads(body, Series.class);

    this.id = series.id;
    this.key = checkNotNull(series.key);
    this.name = checkNotNull(series.name);
    this.tags = checkNotNull(series.tags);
    this.attributes = checkNotNull(series.attributes);
  }

  public String getId() { return id; }
  public void setId(String id) { this.id = checkNotNull(id); }

  public String getKey() { return key; }
  public void setKey(String key) { this.key = checkNotNull(key); }

  public String getName() { return name; }
  public void setName(String name) { this.name = checkNotNull(name); }

  public Set<String> getTags() { return tags; }
  public void setTags(Set<String> tags) { this.tags = checkNotNull(tags); }

  public Map<String, String> getAttributes() { return attributes; }
  public void setAttributes(Map<String, String> attributes) { this.attributes = checkNotNull(attributes); }

  @Override
  public String toString() {
    return String.format("Series(id=%s, key=%s, name=%s, tags=%s, attributes=%s)", id, key, name, tags, attributes);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(119, 123)
      .append(id)
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
      .append(id, rhs.id)
      .append(key, rhs.key)
      .append(name, rhs.name)
      .append(tags, rhs.tags)
      .append(attributes, rhs.attributes)
      .isEquals();
  }
}
