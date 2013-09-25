package com.tempodb;


/**
 *  Folding function for {@link Aggregation}'s and {@link Rollup}'s.
 *  @since 1.0.0
 */
public enum Fold {
  /**
   *  Sum of DataPoints
   *  @since 1.0.0
   */
  SUM,

  /**
   *  Arithmetic mean of DataPoints (average)
   *  @since 1.0.0
   */
  MEAN,

  /**
   *  Maximum of DataPoints
   *  @since 1.0.0
   */
  MAX,

  /**
   *  Minimum of DataPoints
   *  @since 1.0.0
   */
  MIN,

  /**
   *  Count of DataPoints
   *  @since 1.0.0
   */
  COUNT,

  /**
   *  Standard deviation of DataPoints
   *  @since 1.0.0
   */
  STDDEV
}
