package com.tempodb;

import java.io.IOException;
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


public class DataPointSegment extends Segment<DataPoint> {

  private DateTimeZone timezone;
  private Rollup rollup;

  public DataPointSegment() {
    this(new ArrayList(), "", DateTimeZone.UTC, null);
  }

  @JsonCreator
  public DataPointSegment(@JsonProperty("data") List<DataPoint> data, @JsonProperty("tz") DateTimeZone timezone, @JsonProperty("rollup") Rollup rollup) {
    this(data, null, timezone, rollup);
  }

  public DataPointSegment(List<DataPoint> data, String next, DateTimeZone timezone, Rollup rollup) {
    super(data, next);
    this.timezone = checkNotNull(timezone);
    this.rollup = rollup;
  }

  public DataPointSegment(HttpResponse response) throws IOException {
    String body = EntityUtils.toString(response.getEntity());
    DataPointSegment segment = Json.loads(body, DataPointSegment.class);
    PageLinks links = new PageLinks(response);

    this.data = checkNotNull(segment.data);
    this.next = links.getNext();
    this.timezone = checkNotNull(segment.timezone);
    this.rollup = rollup;
  }

  @JsonProperty("tz")
  public DateTimeZone getTimeZone() { return this.timezone; }
  public void setTimeZone(DateTimeZone timezone) { this.timezone = checkNotNull(timezone); }

  public Rollup getRollup() { return this.rollup; }
  public void setRollup(Rollup rollup) { this.rollup = rollup; }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(11, 43)
      .append(data)
      .append(next)
      .append(timezone)
      .append(rollup)
      .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null) return false;
    if(obj == this) return true;
    if(!(obj instanceof DataPointSegment)) return false;

    DataPointSegment rhs = (DataPointSegment)obj;
    return new EqualsBuilder()
      .append(data, rhs.data)
      .append(next, rhs.next)
      .append(timezone, rhs.timezone)
      .append(rollup, rhs.rollup)
      .isEquals();
  }
}
