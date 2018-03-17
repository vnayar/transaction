package com.vnayar.transaction;

import java.time.Clock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * A {@link RollingMetric} based on {@link Statistics} (for min, max, avg, etc.) which uses
 * time as the ID.
 *
 * By default, this RollingMetric has a rolling window of 60 seconds over which it gathers data.
 * As time advances, transactions will expire and no longer influence the result.
 *
 * The time resolution is 1 second, so when each second passes, the transactions that occurred
 * from 61 to 60 seconds ago will expire.
 */
@Component
public class RollingStatisticsMetric
    extends RollingMetric<StatisticsMetric, Transaction, Statistics> {

  private Clock clock;

  @Autowired
  public RollingStatisticsMetric(
      Clock clock,
      @Value("${transaction.maxSeconds:60}") int maxSeconds) {
    super(maxSeconds);
    this.clock = clock;
  }

  /**
   * The last valid ID corresponds to the current timestamp.
   */
  @Override
  protected long getLastId() {
    return clock.millis() / 1000;
  }

  @Override
  protected StatisticsMetric createMetric() {
    return new StatisticsMetric();
  }

  /**
   * The ID of a transaction is the timestamp second in which it occurs.
   */
  @Override
  protected long recordToId(Transaction record) {
    return record.getTimestamp() / 1000;
  }

  @Override
  protected void mergeValues(Statistics a, Statistics b) {
    a.setMin(a.getCount() == 0 ? b.getMin() : Math.min(a.getMin(), b.getMin()));
    a.setMax(a.getCount() == 0 ? b.getMax() : Math.max(a.getMax(), b.getMax()));
    a.setSum(a.getSum() + b.getSum());
    a.setCount(a.getCount() + b.getCount());
    a.setAvg(a.getSum() / a.getCount());
  }
}
