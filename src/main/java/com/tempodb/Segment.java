package com.tempodb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.tempodb.util.Preconditions.*;


class Segment<T> implements Iterable<T> {
  protected List<T> data;
  protected String next;

  public Segment(List<T> data, String next) {
    this.data = checkNotNull(data);
    this.next = next;
  }

  public List<T> getData() { return  this.data; }
  public String getNext() { return this.next; }

  public Iterator<T> iterator() {
    return data.iterator();
  }
}
