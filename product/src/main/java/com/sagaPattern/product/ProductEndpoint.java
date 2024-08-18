package com.sagaPattern.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEndpoint {

  private final ProductService productService;

  // market.product 큐에서 메세지를 받음 (컨슈머 역할)
  @RabbitListener(queues = "${message.queue.product}")
  public void receiveMessage(DeliveryMessage deliveryMessage) {
    productService.reduceProductAmount(deliveryMessage);

    log.info("PRODUCT RECEIVE:{}", deliveryMessage.toString());
  }

  // market.err.product 큐에서 넘어온 메시지 처리
  @RabbitListener(queues="${message.queue.err.product}")
  public void receiveErrorMessage(DeliveryMessage deliveryMessage) {
    log.info("ERROR RECEIVE !!!");
    productService.rollbackProduct(deliveryMessage);
  }

}

