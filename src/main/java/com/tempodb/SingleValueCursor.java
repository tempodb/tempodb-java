package com.tempodb;

import java.net.URI;
import java.util.Iterator;

import org.apache.http.HttpRequest;

import static com.tempodb.util.Preconditions.*;


class SingleValueCursor implements Cursor<SingleValue> {
  private final URI uri;
  private final Client client;

  public SingleValueCursor(URI uri, Client client) {
    this.uri = checkNotNull(uri);
    this.client = checkNotNull(client);
  }

  public Iterator<SingleValue> iterator() {
    HttpRequest request = client.buildRequest(uri.toString());
    Result<SingleValueSegment> result = client.execute(request, SingleValueSegment.class);

    Iterator<SingleValue> iterator = null;
    if(result.getState() == State.SUCCESS) {
      @SuppressWarnings("unchecked") // This cast is always ok
      SegmentIterator<Segment<SingleValue>> segments = new SegmentIterator(client, result.getValue(), SingleValueSegment.class);
      iterator = new SegmentInnerIterator<SingleValue>(segments);
    } else {
      throw new TempoDBException(result.getMessage(), result.getCode());
    }
    return iterator;
  }
}
