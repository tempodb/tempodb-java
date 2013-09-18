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


public class SeriesSegment extends Segment<Series> {

  private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

  public SeriesSegment() {
    this(new ArrayList(), "");
  }

  @JsonCreator
  public SeriesSegment(@JsonProperty("data") List<Series> data) {
    super(data, null);
  }

  public SeriesSegment(List<Series> data, String next) {
    super(data, next);
  }

  public SeriesSegment(HttpResponse response) throws IOException {
    String body = EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);
    SeriesSegment segment = Json.loads(body, SeriesSegment.class);
    PageLinks links = new PageLinks(response);

    this.data = checkNotNull(segment.data);
    this.next = links.getNext();
  }

  @Override
  public String toString() {
    return String.format("SeriesSegment(data=%s, next=%s)", data, next);
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
    if(!(obj instanceof SeriesSegment)) return false;

    SeriesSegment rhs = (SeriesSegment)obj;
    return new EqualsBuilder()
      .append(data, rhs.data)
      .append(next, rhs.next)
      .isEquals();
  }
}
