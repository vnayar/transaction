package com.vnayar.transaction.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

import com.vnayar.transaction.Statistics;
import com.vnayar.transaction.Transaction;
import java.time.Clock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Normally integration tests should be moved into a separate build target, as integration tests
 * can take quite a bit of time to initialize.
 */
@Profile("integration")
@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {
  private final static long INITIAL_TIME = 12341234000L;

  @MockBean
  public Clock clock;

  @Autowired
  private TestRestTemplate restTemplate;

  @Before
  public void init() {
    when(clock.millis()).thenReturn(INITIAL_TIME);
  }

  @Test
  public void transactions_valid() throws Exception {
    ResponseEntity<Void> responseEntity = restTemplate.postForEntity(
        "/transactions",
        new Transaction(100.00, INITIAL_TIME - 2_000),
        Void.class);
    assertThat(responseEntity.getStatusCodeValue(), equalTo(200));
  }

  @Test
  public void transactions_invlaidFutureTime() throws Exception {
    ResponseEntity<Void> responseEntity = restTemplate.postForEntity(
        "/transactions",
        new Transaction(100.00, INITIAL_TIME + 2_000),
        Void.class);

    assertThat(responseEntity.getStatusCodeValue(), equalTo(204));
  }

  @Test
  public void transactions_expiredTime() throws Exception {
    ResponseEntity<Void> responseEntity = restTemplate.postForEntity(
        "/transactions",
        new Transaction(100.00, INITIAL_TIME - 64_000),
        Void.class);

    assertThat(responseEntity.getStatusCodeValue(), equalTo(204));
  }

  @Test
  public void statistics_valid() throws Exception {
    restTemplate.postForEntity(
        "/transactions",
        new Transaction(100.00, INITIAL_TIME - 30_000),
        Void.class);
    restTemplate.postForEntity(
        "/transactions",
        new Transaction(200.00, INITIAL_TIME - 20_000),
        Void.class);
    restTemplate.postForEntity(
        "/transactions",
        new Transaction(300.00, INITIAL_TIME - 10_000),
        Void.class);

    ResponseEntity<Statistics> responseEntity = restTemplate.getForEntity(
        "/statistics", Statistics.class);

    assertThat(responseEntity.getStatusCodeValue(), equalTo(200));

    Statistics stats = responseEntity.getBody();
    assertThat(stats.getCount(), equalTo(3L));
  }
}
