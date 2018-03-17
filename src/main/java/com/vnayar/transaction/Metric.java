package com.vnayar.transaction;

/**
 * A general purpose metric accumulator.
 * @param <RecordT> The record type about which metrics are recorded.
 * @param <ValueT> The cumulative metric value produced about the records.
 */
public interface Metric<RecordT, ValueT> {

  /**
   * Updates the cumulative metric with an additional record.
   */
  void update(RecordT record);

  /**
   * Retrieves the cumulative metric value computed from all received records thus far.
   */
  ValueT getValue();

  /**
   * Sets the cumulative metric value back to its default state.
   */
  void reset();
}
