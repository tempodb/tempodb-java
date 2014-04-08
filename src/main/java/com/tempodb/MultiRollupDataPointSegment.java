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


public class MultiRollupDataPointSegment extends Segment<MultiDataPoint> {

  private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

  private DateTimeZone timezone;
  private MultiRollup rollup;

  public MultiRollupDataPointSegment() {
    this(new ArrayList<MultiDataPoint>(), "", DateTimeZone.UTC, null);
  }

  @JsonCreator
  public MultiRollupDataPointSegment(@JsonProperty("data") List<MultiDataPoint> data, @JsonProperty("tz") DateTimeZone timezone, @JsonProperty("rollup") MultiRollup rollup) {
    this(data, null, timezone, rollup);
  }

  public MultiRollupDataPointSegment(List<MultiDataPoint> data, String next, DateTimeZone timezone, MultiRollup rollup) {
    super(data, next);
    this.timezone = checkNotNull(timezone);
    this.rollup = rollup;
  }

  @JsonProperty("tz")
  public DateTimeZone getTimeZone() { return this.timezone; }
  public void setTimeZone(DateTimeZone timezone) { this.timezone = checkNotNull(timezone); }

  public MultiRollup getRollup() { return this.rollup; }
  public void setRollup(MultiRollup rollup) { this.rollup = rollup; }

  static MultiRollupDataPointSegment make(HttpResponse response) throws IOException {
    String body = EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);
    MultiRollupDataPointSegment segment = Json.loads(body, MultiRollupDataPointSegment.class);
    PageLinks links = new PageLinks(response);
    segment.next = links.getNext();
    return segment;
  }

  @Override
  public String toString() {
    return String.format("MultiRollupDataPointSegment(data=%s, next=%s, timezone=%s, rollup=%s", data, next, timezone, rollup);
  }

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
    if(!(obj instanceof MultiRollupDataPointSegment)) return false;

    MultiRollupDataPointSegment rhs = (MultiRollupDataPointSegment)obj;
    return new EqualsBuilder()
      .append(data, rhs.data)
      .append(next, rhs.next)
      .append(timezone, rhs.timezone)
      .append(rollup, rhs.rollup)
      .isEquals();
  }
}
