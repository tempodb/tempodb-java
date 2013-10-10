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
   *  Multiplication of DataPoints
   *  @since 1.0.0
   */
  MULT,

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
   *  Variance of DataPoints
   *  @since 1.0.0
   */
  VARIANCE,

  /**
   *  Standard deviation of DataPoints
   *  @since 1.0.0
   */
  STDDEV,

  /**
   *  Range of DataPoints (max - min)
   *  @since 1.0.0
   */
  RANGE,

  /**
   *  First DataPoint in interval (takes the left most DataPoint)
   *  @since 1.0.0
   */
  FIRST
}
