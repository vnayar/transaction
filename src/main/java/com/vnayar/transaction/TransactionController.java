package com.vnayar.transaction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * A simple controller for the two REST endpoints.
 */
@RestController
@Slf4j
public class TransactionController {

  private RollingStatisticsMetric rollingStatisticsMetric;

  @Autowired
  TransactionController(RollingStatisticsMetric rollingStatisticsMetric) {
    this.rollingStatisticsMetric = rollingStatisticsMetric;
  }

  /**
   * Receive incoming transactions and compute metrics about them in O(1) time/memory.
   */
  @PostMapping("/transactions")
  void postTransaction(@RequestBody Transaction transaction) {
    log.debug("Incoming transaction: {}", transaction);
    rollingStatisticsMetric.update(transaction);
  }

  /**
   * Present statistics about transactions in the last 60 seconds in O(1) time/memory.
   */
  @GetMapping("/statistics")
  Statistics getStatistics() {
    Statistics stats = rollingStatisticsMetric.getValue();
    log.debug("Returning statistics: {}", stats);
    return stats;
  }
}
