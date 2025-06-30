package com.store.backend.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.store.backend.config.RabbitMQConfig;
import com.store.backend.order.dto.OrderEmailDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublishMessage {
  private final RabbitTemplate rabbitTemplate;

  public void sendOrderEmail(OrderEmailDto message) {
    rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, message);
    log.info("Đã truyền message cho đơn hàng: {}", message.getOrderId());
  }
}
