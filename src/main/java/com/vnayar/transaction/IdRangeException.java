package com.vnayar.transaction;

/**
 * An error for when a resource has been accessed with an invalid ID.
 * E.g. the id is no longer current, it is out of array bounds, has not been created yet, etc.
 */
public class IdRangeException extends RuntimeException {

  public IdRangeException() {
    super();
  }

  public IdRangeException(String s) {
    super(s);
  }

  public IdRangeException(String s, Throwable throwable) {
    super(s, throwable);
  }
}
