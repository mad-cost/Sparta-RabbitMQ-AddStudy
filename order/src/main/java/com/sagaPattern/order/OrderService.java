package com.sagaPattern.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

  @Value("${message.queue.product}")
  private String productQueue;

  private final RabbitTemplate rabbitTemplate;

  // Order 객체 Map에 저장
  private Map<UUID, Order> orderStore = new HashMap<>();

  public Order createOrder(OrderEndpoint.OrderRequestDto orderRequestDto) {
    Order order = orderRequestDto.toOrder(); // Order 객체 생성
    // DeliveryMessage 객체를 만들어서 메시지 보내기
    DeliveryMessage deliveryMessage = orderRequestDto.toDeliveryMessage(order.getOrderId());
    // Order 객체 Map에 저장
    orderStore.put(order.getOrderId(), order);

    log.info("send Message : {}",deliveryMessage.toString());

    // 큐에 메시지(객체) 보내기
    rabbitTemplate.convertAndSend(productQueue, deliveryMessage);

    return order;

  }

  public void rollbackOrder(DeliveryMessage message) {
    Order order = orderStore.get(message.getOrderId());
    order.cancelOrder(message.getErrorType());
    log.info(order.toString());

  }

  public Order getOrder(UUID orderId) {
    return orderStore.get(orderId);
  }
}