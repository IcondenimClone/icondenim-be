package com.store.backend.exception;

public class TooManyException extends RuntimeException {
  public TooManyException(String message) {
    super(message);
  }
}
