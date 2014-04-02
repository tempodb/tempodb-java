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
import org.joda.time.DateTimeZone;

import com.tempodb.http.PageLinks;
import com.tempodb.json.Json;
import static com.tempodb.util.Preconditions.*;


public class DataPointFoundSegment extends Segment<DataPointFound> {

  private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

  private DateTimeZone timezone;
  private Predicate predicate;

  public DataPointFoundSegment() {
    this(new ArrayList<DataPointFound>(), "", DateTimeZone.UTC, null);
  }

  @JsonCreator
  public DataPointFoundSegment(@JsonProperty("data") List<DataPointFound> data, @JsonProperty("tz") DateTimeZone timezone, @JsonProperty("predicate") Predicate predicate) {
    this(data, null, timezone, predicate);
  }

  public DataPointFoundSegment(List<DataPointFound> data, String next, DateTimeZone timezone, Predicate predicate) {
    super(data, next);
    this.timezone = checkNotNull(timezone);
    this.predicate = predicate;
  }

  @JsonProperty("tz")
  public DateTimeZone getTimeZone() { return this.timezone; }
  public void setTimeZone(DateTimeZone timezone) { this.timezone = checkNotNull(timezone); }

  public Predicate getPredicate() { return this.predicate; }
  public void setPredicate(Predicate predicate) { this.predicate = predicate; }

  static DataPointFoundSegment make(HttpResponse response) throws IOException {
    String body = EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);
    DataPointFoundSegment segment = Json.loads(body, DataPointFoundSegment.class);
    PageLinks links = new PageLinks(response);
    segment.next = links.getNext();
    return segment;
  }

  @Override
  public String toString() {
    return String.format("DataPointFoundSegment(data=%s, next=%s, timezone=%s, predicate=%s", data, next, timezone, predicate);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(11, 43)
      .append(data)
      .append(next)
      .append(timezone)
      .append(predicate)
      .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null) return false;
    if(obj == this) return true;
    if(!(obj instanceof DataPointFoundSegment)) return false;

    DataPointFoundSegment rhs = (DataPointFoundSegment)obj;
    return new EqualsBuilder()
      .append(data, rhs.data)
      .append(next, rhs.next)
      .append(timezone, rhs.timezone)
      .append(predicate, rhs.predicate)
      .isEquals();
  }
}
