package com.store.backend.exception;

public class BadCredentialsException extends RuntimeException {
  public BadCredentialsException(String message) {
    super(message);
  }
}
