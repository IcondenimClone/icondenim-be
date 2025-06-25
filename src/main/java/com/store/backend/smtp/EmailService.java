package com.store.backend.smtp;

import com.store.backend.order.entity.OrderEntity;

public interface EmailService {
  void sendAuthEmail(String to, String subject, String otp);

  void sendOrderEmail(String to, String subject, OrderEntity order, String confirmLink);
}
