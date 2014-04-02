package com.tempodb;

import java.net.URI;
import java.util.Iterator;

import org.apache.http.HttpRequest;

import static com.tempodb.util.Preconditions.*;


class DataPointFoundCursor implements Cursor<DataPointFound> {
  private final URI uri;
  private final Client client;

  public DataPointFoundCursor(URI uri, Client client) {
    this.uri = checkNotNull(uri);
    this.client = checkNotNull(client);
  }

  public Iterator<DataPointFound> iterator() {
    HttpRequest request = client.buildRequest(uri.toString());
    Result<DataPointFoundSegment> result = client.execute(request, DataPointFoundSegment.class);

    Iterator<DataPointFound> iterator = null;
    if(result.getState() == State.SUCCESS) {
      @SuppressWarnings("unchecked") // This cast is always ok
      SegmentIterator<Segment<DataPointFound>> segments = new SegmentIterator(client, result.getValue(), DataPointFoundSegment.class);
      iterator = new SegmentInnerIterator<DataPointFound>(segments);
    } else {
      throw new TempoDBException(result.getMessage(), result.getCode());
    }
    return iterator;
  }
}
