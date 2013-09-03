package com.tempodb;

import java.util.Iterator;


public class Cursor<T> implements Iterator<T> {
  private Iterator<Segment<T>> segments;
  private Iterator<T> currentSegment;

  public Cursor(SegmentIterator<T> segments) {
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
  public T next() {
    return currentSegment.next();
  }

  @Override public final void remove() {
    throw new UnsupportedOperationException();
  }
}
