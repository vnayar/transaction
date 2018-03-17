package com.vnayar.transaction;

/**
 * Logic to cumulatively collect statistics about incoming transactions.
 */
public class StatisticsMetric implements Metric<Transaction, Statistics> {

  private Statistics statistics;

  public StatisticsMetric() {
    reset();
  }

  @Override
  public void update(Transaction transaction) {
    long cnt = statistics.getCount();
    double amt = transaction.getAmount();

    statistics.setSum(statistics.getSum() + amt);
    statistics.setMax(cnt == 0 ? amt : Math.max(amt, statistics.getMax()));
    statistics.setMin(cnt == 0 ? amt : Math.min(amt, statistics.getMin()));
    statistics.setCount(cnt + 1);
    statistics.setAvg(statistics.getSum() / statistics.getCount());
  }

  @Override
  public Statistics getValue() {
    return statistics;
  }

  @Override
  public void reset() {
    statistics = new Statistics();
  }
}
