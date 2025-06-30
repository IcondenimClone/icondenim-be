package com.store.backend.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.store.backend.config.RabbitMQConfig;
import com.store.backend.order.dto.OrderEmailDto;
import com.store.backend.user.dto.AuthEmailDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublishMessage {
  private final RabbitTemplate rabbitTemplate;

  public void sendAuthEmail(AuthEmailDto message) {
    rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.AUTH_ROUTING_KEY, message);
    log.info("Đã truyền AuthEmailDto tới queue: {}", message.getTo());
  }

  public void sendOrderEmail(OrderEmailDto message) {
    rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ORDER_ROUTING_KEY, message);
    log.info("Đã truyền OrderEmailDto tới queue: {}", message.getOrderId());
  }
}
