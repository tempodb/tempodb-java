package com.tempodb;

import java.util.Iterator;
import java.util.List;


public class Segment<T> implements Iterable<T> {
  private List<T> data;
  private String next;

  public Segment(List<T> data, String next) {
    this.data = data;
    this.next = next;
  }

  public Iterator<T> iterator() {
    return data.iterator();
  }
}
