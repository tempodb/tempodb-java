package com.tempodb;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static com.tempodb.util.Preconditions.*;
import org.apache.http.HttpRequest;


public class SegmentIterator<T extends Segment<?>> implements Iterator<T> {

  private final Client client;
  private T segment;
  private Class<T> klass;

  public SegmentIterator(Client client, T initial, Class<T> klass) {
    this.client = checkNotNull(client);
    this.segment = checkNotNull(initial);
    this.klass =  klass;
  }

  @Override
  public final T next() {
    if(!hasNext()) {
      throw new NoSuchElementException();
    }
    T rv = this.segment;

    try {
      if(!this.segment.equals(null) && !this.segment.equals("")) {
        HttpRequest request = client.buildRequest(this.segment.getNext());
        Result<T> result = client.execute(request, klass);
        if(result.isSuccessful()) {
          this.segment = result.getValue();
        } else {
          throw new TempoDBApiException();
        }
      } else {
        this.segment = null;
      }
    }
    catch (UnsupportedEncodingException e) {
      throw new TempoDBApiException();
    }
    catch (IOException e) {
      throw new TempoDBApiException();
    }
    return rv;
  }

  @Override
  public final boolean hasNext() {
    return segment != null;
  }

  @Override public final void remove() {
    throw new UnsupportedOperationException();
  }
}
