package com.vnayar.transaction;

import lombok.Data;

/**
 * The output of the server, which shows basic statistics about transactions.
 */
@Data
public class Statistics {
  private double sum;
  private double avg;
  private double max;
  private double min;
  private long count;
}
