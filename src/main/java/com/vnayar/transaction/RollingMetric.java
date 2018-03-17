package com.vnayar.transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.StampedLock;
import lombok.Data;

/**
 * A cumulative metric that only accepts a rolling range of valid record IDs.
 *
 * Implementations must specify how the ID of a record is computed as well as the last valid ID
 * and the span of valid IDs. For example, if the lastValidId is 100 and the span is 20, then only
 * records with IDs 80 to 100 are part of the metric.
 *
 * The lastValidId may vary through the lifetime of this object.
 */
public abstract class RollingMetric<MetricT extends Metric<RecordT, ValueT>, RecordT, ValueT>
    implements Metric<RecordT, ValueT> {

  /**
   * A bucket stores the value of a metric for a given ID.  If the lastId changes, a bucket's
   * metric may become reset.
   */
  @Data
  class Bucket {
    private long id;
    private MetricT metric;
    StampedLock lock;

    Bucket(long id, MetricT metric) {
      this.id = id;
      this.metric = metric;
      this.lock = new StampedLock();
    }
  }
  private List<Bucket> buckets;

  /**
   * @param idSpan Sets how many IDs before the value of {@link #getLastId} are part of the metric.
   */
  public RollingMetric(int idSpan) {
    buckets = new ArrayList<>(idSpan);
    for (int i = 0; i < idSpan; i++) {
      buckets.add(i, new Bucket(Long.MIN_VALUE, createMetric()));
    }
  }

  /**
   * @return A newly initialized metric.
   */
  protected abstract MetricT createMetric();

  /**
   * Converts a record into a an ID.
   */
  protected abstract long recordToId(RecordT record);

  /**
   * Defines how the values of metrics with different IDs can be merged.
   */
  protected abstract void mergeValues(ValueT a, ValueT b);

  /**
   * Provides the most last valid ID.
   *
   * This may change in sequential calls, e.g. if the time is used as an ID.
   */
  protected abstract long getLastId();

  @Override
  public void update(RecordT record) {
    long id = recordToId(record);
    long lastId = getLastId();
    // Abort if the id of the record is out of the range stored in this metric.
    if (id > lastId || id <= lastId - buckets.size()) {
      throw new IdRangeException("Record ID " + id + " is out of range.");
    }
    // Update the appropriate bucket.
    int bucketIndex = (int)(recordToId(record) % buckets.size());
    Bucket bucket = buckets.get(bucketIndex);
    // Lock access to the bucket to prevent two different threads from resetting it after valid
    // data has been written.
    long stamp = bucket.getLock().writeLock();
    if (bucket.getId() != id) {
      bucket.getMetric().reset();
      bucket.setId(id);
    }
    bucket.getMetric().update(record);
    bucket.getLock().unlockWrite(stamp);
  }

  @Override
  public ValueT getValue() {
    ValueT value = createMetric().getValue();
    long lastId = getLastId();
    for (Bucket bucket : buckets) {
      if (bucket.getId() <= lastId && bucket.getId() > lastId - buckets.size()) {
        // Reads can happen simultaneously, just not while writing.
        long stamp = bucket.getLock().readLock();
        mergeValues(value, bucket.getMetric().getValue());
        bucket.getLock().unlockRead(stamp);
      }
    }
    return value;
  }


  @Override
  public void reset() {
    for (Bucket bucket : buckets) {
      long stamp = bucket.getLock().writeLock();
      bucket.setId(Long.MIN_VALUE);
      bucket.getMetric().reset();
      bucket.getLock().unlockWrite(stamp);
    }
  }
}
