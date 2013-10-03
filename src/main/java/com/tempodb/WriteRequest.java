package com.tempodb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static com.tempodb.util.Preconditions.*;


/**
 *  A request for writing multiple DataPoints to multiple Series.
 *  <p>The request is created and datapoints are added for a Series.
 *  @since 1.0.0
 */
public class WriteRequest implements Iterable<MultiDataPoint> {

  private final List<MultiDataPoint> data;

  /**
   *  Base constructor
   *  @since 1.0.0
   */
  public WriteRequest() {
    this.data = new ArrayList<MultiDataPoint>();
  }

  /**
   *  Adds a DataPoint to the request for a Series.
   *  @param series The Series to write to.
   *  @param datapoint The DataPoint to write.
   *  @return The updated request.
   *  @since 1.0.0
   */
  public WriteRequest add(Series series, DataPoint datapoint) {
    MultiDataPoint mdp = new MultiDataPoint(series, datapoint.getTimestamp(), datapoint.getValue());
    data.add(mdp);
    return this;
  }

  @Override
  public Iterator<MultiDataPoint> iterator() {
    return data.iterator();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(257, 263)
      .append(data)
      .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null) return false;
    if(obj == this) return true;
    if(!(obj instanceof WriteRequest)) return false;

    WriteRequest rhs = (WriteRequest)obj;
    return new EqualsBuilder()
      .append(data, rhs.data)
      .isEquals();
  }
}
