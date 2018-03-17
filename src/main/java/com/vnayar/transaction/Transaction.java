package com.vnayar.transaction;

import lombok.Data;

/**
 * The input data, a simple transaction of a certain amount of a currency at a point in time.
 */
@Data
public class Transaction {
  private final double amount;
  private final long timestamp;
}
