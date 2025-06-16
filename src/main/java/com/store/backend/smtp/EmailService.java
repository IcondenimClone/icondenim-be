package com.store.backend.smtp;

public interface EmailService {
  void sendVerifySignUpEmail(String to, String subject, String otp);
}
