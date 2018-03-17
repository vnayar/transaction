package com.vnayar.transaction;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

import java.time.Clock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 */
@RunWith(SpringRunner.class)
public class RollingStatisticsMetricTest {
  @Mock
  private Clock clock;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private final static double DELTA = 0.0001;
  private final static long INITIAL_TIME = 12341234000L;
  private final static int MAX_SECONDS = 60;

  private RollingStatisticsMetric rollingStatisticsMetric;

  @Before
  public void init() {
    when(clock.millis()).thenReturn(INITIAL_TIME);
    rollingStatisticsMetric = new RollingStatisticsMetric(clock, MAX_SECONDS);
  }

  @Test
  public void update_validTransactions() {
    rollingStatisticsMetric.update(new Transaction(100.00, INITIAL_TIME - 2_300));
    rollingStatisticsMetric.update(new Transaction(120.00, INITIAL_TIME - 2_700));
    rollingStatisticsMetric.update(new Transaction(80.00, INITIAL_TIME - 27_000));
    rollingStatisticsMetric.update(new Transaction(25.00, INITIAL_TIME - 45_200));
    rollingStatisticsMetric.update(new Transaction(35.00, INITIAL_TIME - 45_500));

    Statistics stats = rollingStatisticsMetric.getValue();
    assertThat(stats.getCount(), equalTo(5L));
    assertThat(stats.getMin(), closeTo(25.00, DELTA));
    assertThat(stats.getMax(), closeTo(120.00, DELTA));
    assertThat(stats.getSum(), closeTo(360.00, DELTA));
    assertThat(stats.getAvg(), closeTo(72.00, DELTA));
  }

  @Test
  public void update_rejectFutureTransactions() {
    expectedException.expect(IdRangeException.class);
    rollingStatisticsMetric.update(new Transaction(100.00, INITIAL_TIME + 2_300));
  }

  @Test
  public void update_rejectTooOldTransactions() {
    expectedException.expect(IdRangeException.class);
    rollingStatisticsMetric.update(new Transaction(100.00, INITIAL_TIME - 62_300));
  }
}