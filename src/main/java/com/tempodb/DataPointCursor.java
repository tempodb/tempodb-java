package com.tempodb;

import java.net.URI;
import java.util.Iterator;

import org.apache.http.HttpRequest;

import static com.tempodb.util.Preconditions.*;


public class DataPointCursor implements Cursor<DataPoint> {
  private final URI uri;
  private final Client client;

  public DataPointCursor(URI uri, Client client) {
    this.uri = checkNotNull(uri);
    this.client = checkNotNull(client);
  }

  public Iterator<DataPoint> iterator() {
    HttpRequest request = client.buildRequest(uri.toString());
    Result<DataPointSegment> result = client.execute(request, DataPointSegment.class);

    Iterator<DataPoint> iterator = null;
    if(result.isSuccessful()) {
      SegmentIterator<Segment<DataPoint>> segments = new SegmentIterator(client, result.getValue(), DataPointSegment.class);
      iterator = new DataPointIterator(segments);
    } else {
      throw new TempoDBException(result.getMessage(), result.getCode());
    }
    return iterator;
  }

  private class DataPointIterator implements Iterator<DataPoint> {
    private Iterator<Segment<DataPoint>> segments;
    private Iterator<DataPoint> currentSegment;

    public DataPointIterator(SegmentIterator<Segment<DataPoint>> segments) {
      this.segments = segments;
      currentSegment = null;
    }

    @Override
    public boolean hasNext() {
      boolean hasNext = true;
      if(currentSegment == null) {
        if(segments.hasNext()) {
          currentSegment = segments.next().iterator();
        } else {
          return false;
        }
      }

      while(!currentSegment.hasNext() && segments.hasNext()) {
        currentSegment = segments.next().iterator();
      }
      return currentSegment.hasNext();
    }

    @Override
    public DataPoint next() {
      return currentSegment.next();
    }

    @Override public final void remove() {
      throw new UnsupportedOperationException();
    }
  }
}
