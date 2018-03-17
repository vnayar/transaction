package com.vnayar.transaction;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 */
@RunWith(SpringRunner.class)
public class StatisticsMetricTest {
  private StatisticsMetric statisticsMetric = new StatisticsMetric();
  private static double DELTA = 0.0001;

  @Test
  public void initialState() throws Exception {
    Statistics stats = statisticsMetric.getValue();
    assertThat(stats.getCount(), equalTo(0L));
    assertThat(stats.getMin(), closeTo(0.0, DELTA));
    assertThat(stats.getMax(), closeTo(0.0, DELTA));
    assertThat(stats.getSum(), closeTo(0.0, DELTA));
    assertThat(stats.getAvg(), closeTo(0.0, DELTA));
  }

  @Test
  public void update() throws Exception {
    statisticsMetric.update(new Transaction(100.00, 1521289761000L));
    statisticsMetric.update(new Transaction(150.00, 1521289761001L));
    statisticsMetric.update(new Transaction(50.00, 1521289761002L));
    Statistics stats = statisticsMetric.getValue();

    assertThat(stats.getCount(), equalTo(3L));
    assertThat(stats.getMin(), closeTo(50.0, DELTA));
    assertThat(stats.getMax(), closeTo(150.0, DELTA));
    assertThat(stats.getSum(), closeTo(300.0, DELTA));
    assertThat(stats.getAvg(), closeTo(100.0, DELTA));
  }

  @Test
  public void reset() throws Exception {
    statisticsMetric.update(new Transaction(100, 1521289761000L));
    statisticsMetric.reset();
    Statistics stats = statisticsMetric.getValue();
    assertThat(stats.getCount(), equalTo(0L));
    assertThat(stats.getMin(), closeTo(0.0, DELTA));
    assertThat(stats.getMax(), closeTo(0.0, DELTA));
    assertThat(stats.getSum(), closeTo(0.0, DELTA));
    assertThat(stats.getAvg(), closeTo(0.0, DELTA));
  }

}