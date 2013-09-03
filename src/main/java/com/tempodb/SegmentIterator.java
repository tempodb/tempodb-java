package com.tempodb;

import java.util.Iterator;
import java.util.NoSuchElementException;


public class SegmentIterator<T> implements Iterator<Segment<T>> {

  private final Client client;
  private Segment<T> segment;

  public SegmentIterator(Client client, Segment<T> initial) {
    if(initial == null) {
      throw new IllegalArgumentException();
    }

    this.client = client;
    this.segment = initial;
  }

  @Override
  public final Segment<T> next() {
    if(!hasNext()) {
      throw new NoSuchElementException();
    }
    Segment<T> rv = this.segment;

    // Get the next segment
    segment = null;
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
