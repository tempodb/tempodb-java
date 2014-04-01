package com.tempodb;

import java.net.URI;
import java.util.Iterator;

import org.apache.http.HttpRequest;

import static com.tempodb.util.Preconditions.*;


class MultiDataPointCursor implements Cursor<MultiDataPoint> {
  private final URI uri;
  private final Client client;

  public MultiDataPointCursor(URI uri, Client client) {
    this.uri = checkNotNull(uri);
    this.client = checkNotNull(client);
  }

  public Iterator<MultiDataPoint> iterator() {
    HttpRequest request = client.buildRequest(uri.toString());
    Result<MultiDataPointSegment> result = client.execute(request, MultiDataPointSegment.class);

    Iterator<MultiDataPoint> iterator = null;
    if(result.getState() == State.SUCCESS) {
      @SuppressWarnings("unchecked") // This cast is always ok
      SegmentIterator<Segment<MultiDataPoint>> segments = new SegmentIterator(client, result.getValue(), MultiDataPointSegment.class);
      iterator = new SegmentInnerIterator<MultiDataPoint>(segments);
    } else {
      throw new TempoDBException(result.getMessage(), result.getCode());
    }
    return iterator;
  }
}
