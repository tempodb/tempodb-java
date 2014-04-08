package com.tempodb;

import java.net.URI;
import java.util.Iterator;

import org.apache.http.HttpRequest;

import static com.tempodb.util.Preconditions.*;


class MultiRollupDataPointCursor implements Cursor<MultiDataPoint> {
  private final URI uri;
  private final Client client;

  public MultiRollupDataPointCursor(URI uri, Client client) {
    this.uri = checkNotNull(uri);
    this.client = checkNotNull(client);
  }

  public Iterator<MultiDataPoint> iterator() {
    HttpRequest request = client.buildRequest(uri.toString());
    Result<MultiRollupDataPointSegment> result = client.execute(request, MultiRollupDataPointSegment.class);

    Iterator<MultiDataPoint> iterator = null;
    if(result.getState() == State.SUCCESS) {
      @SuppressWarnings("unchecked") // This cast is always ok
      SegmentIterator<Segment<MultiDataPoint>> segments = new SegmentIterator(client, result.getValue(), MultiRollupDataPointSegment.class);
      iterator = new SegmentInnerIterator<MultiDataPoint>(segments);
    } else {
      throw new TempoDBException(result.getMessage(), result.getCode());
    }
    return iterator;
  }
}
