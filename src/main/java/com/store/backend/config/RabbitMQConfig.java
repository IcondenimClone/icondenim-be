package com.store.backend.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
  public static final String AUTH_QUEUE = "icondenim.auth.queue";
  public static final String ORDER_QUEUE = "icondenim.order.queue";

  public static final String EXCHANGE_NAME = "icondenim.email.exchange";

  public static final String AUTH_ROUTING_KEY = "icondenim-auth-routing-key";
  public static final String ORDER_ROUTING_KEY = "icondenim-order-routing-key";

  @Bean
  public Queue authQueue() {
    return new Queue(AUTH_QUEUE, true);
  }

  @Bean
  public Queue orderQueue() {
    return new Queue(ORDER_QUEUE, true);
  }

  @Bean
  public TopicExchange exchange() {
    return new TopicExchange(EXCHANGE_NAME);
  }

  @Bean
  public Binding bindingAuthQueue(Queue authQueue, TopicExchange exchange) {
    return BindingBuilder.bind(authQueue).to(exchange).with(AUTH_ROUTING_KEY);
  }

  @Bean
  public Binding bindingOrderQueue(Queue orderQueue, TopicExchange exchange) {
    return BindingBuilder.bind(orderQueue).to(exchange).with(ORDER_ROUTING_KEY);
  }

  @Bean
  public Jackson2JsonMessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(jsonMessageConverter());
    return rabbitTemplate;
  }
}
