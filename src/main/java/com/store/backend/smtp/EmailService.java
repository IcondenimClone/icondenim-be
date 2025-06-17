package com.store.backend.smtp;

public interface EmailService {
  void sendAuthEmail(String to, String subject, String otp);
}
