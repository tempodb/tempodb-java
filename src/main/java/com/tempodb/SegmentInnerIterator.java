package com.tempodb;

import java.util.Iterator;


public class SegmentInnerIterator<T> implements Iterator<T> {
  private Iterator<Segment<T>> segments;
  private Iterator<T> currentSegment;

  public SegmentInnerIterator(SegmentIterator<Segment<T>> segments) {
    this.segments = segments;
    currentSegment = null;
  }

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

  public T next() {
    return currentSegment.next();
  }

  public final void remove() {
    throw new UnsupportedOperationException();
  }
}
