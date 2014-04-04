package com.tempodb;


/**
 *  Direction to search for Single Value calls.
 *  @since 1.1.0
 */
public enum Direction {
  /**
   *  Before the timestamp
   *  @since 1.1.0
   */
  BEFORE,

  /**
   *  After the timestamp
   *  @since 1.1.0
   */
  AFTER,

  /**
   *  Nearest the timestamp
   *  @since 1.1.0
   */
  NEAREST,

  /**
   *  Exact timestamp match
   *  @since 1.1.0
   */
  EXACT
}
