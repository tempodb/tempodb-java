package com.tempodb;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import com.tempodb.http.PageLinks;
import com.tempodb.json.Json;
import static com.tempodb.util.Preconditions.*;


public class SingleValueSegment extends Segment<SingleValue> {

  private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

  public SingleValueSegment() {
    this(new ArrayList<SingleValue>(), "");
  }

  @JsonCreator
  public SingleValueSegment(@JsonProperty("data") List<SingleValue> data) {
    super(data, null);
  }

  public SingleValueSegment(List<SingleValue> data, String next) {
    super(data, next);
  }

  static SingleValueSegment make(HttpResponse response) throws IOException {
    String body = EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);
    SingleValueSegment segment = Json.loads(body, SingleValueSegment.class);
    PageLinks links = new PageLinks(response);
    segment.next = links.getNext();
    return segment;
  }

  @Override
  public String toString() {
    return String.format("SingleValueSegment(data=%s, next=%s)", data, next);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(127, 129)
      .append(data)
      .append(next)
      .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null) return false;
    if(obj == this) return true;
    if(!(obj instanceof SingleValueSegment)) return false;

    SingleValueSegment rhs = (SingleValueSegment)obj;
    return new EqualsBuilder()
      .append(data, rhs.data)
      .append(next, rhs.next)
      .isEquals();
  }
}
