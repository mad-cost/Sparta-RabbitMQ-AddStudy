package com.sagaPattern.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

  private final RabbitTemplate rabbitTemplate;

  @Value("${message.queue.payment}")
  private String paymentQueue;

  @Value("${message.queue.err.order}")
  private String orderErrorQueue;

  // Order 큐를 통해 받은 상품의 수량 감소
  public void reduceProductAmount(DeliveryMessage deliveryMessage) {
    Integer productId = deliveryMessage.getProductId();
    Integer productQuantity = deliveryMessage.getProductQuantity(); // 상품 수량

    // order 에서 받은 market.product 큐의 메시지에 문제가 있을 경우
    if (productId != 1 || productQuantity > 1) {
      this.rollbackProduct(deliveryMessage);
      return;
    }
    // 문제가 없다면 market.payment 큐로 메시지를 보내준다
    rabbitTemplate.convertAndSend(paymentQueue,deliveryMessage);
  }

  public void rollbackProduct(DeliveryMessage deliveryMessage){
    log.info("PRODUCT ROLLBACK!!!");
    // order 에서 받은 market.product 큐의 메시지에 문제가 있을 경우 / getErrorType에 값이 없다면
    if(!StringUtils.hasText(deliveryMessage.getErrorType())){
      // PRODUCT 에서 에러 발생
      deliveryMessage.setErrorType("PRODUCT ERROR");
    }
    // market.err.order 큐로 에러 메시지 전송
    rabbitTemplate.convertAndSend(orderErrorQueue, deliveryMessage);
  }

}
