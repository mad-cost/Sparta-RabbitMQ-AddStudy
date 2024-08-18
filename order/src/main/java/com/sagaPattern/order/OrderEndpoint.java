package com.sagaPattern.order;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderEndpoint {

  private final OrderService orderService;

  private final RabbitTemplate rabbitTemplate;

  @GetMapping("order/{orderId}")
  public ResponseEntity<Order> getOrder(
          @PathVariable
          UUID orderId
  ) {
    Order order = orderService.getOrder(orderId);
    return ResponseEntity.ok(order);
  }

  @PostMapping("/order")
  public ResponseEntity<Order> order(
          @RequestBody
          OrderRequestDto orderRequestDto // 아래서 메서드로 만들어 줬다
  ) {
    Order order = orderService.createOrder(orderRequestDto);
    return ResponseEntity.ok(order);
  }

  @RabbitListener(queues = "${message.queue.err.order}")
  public void errOrder(DeliveryMessage message) {
    log.info("ERROR RECEIVE !!!");
    orderService.rollbackOrder(message);
  }

  @Data
  public static class OrderRequestDto {
    private String userId;
    private Integer productId;
    private Integer productQuantity; // 상품 수량
    private Integer payAmount; // 상품 가격

    // Order 객체 생성
    public Order toOrder (){
      return Order.builder()
              .orderId(UUID.randomUUID())
              .userId(userId)
              .orderStatus("RECEIPT")
              .build();

    }

    // DeliveryMessage 객체 생성
    public DeliveryMessage toDeliveryMessage(UUID orderId){
      return DeliveryMessage.builder()
              .orderId(orderId)
              .productId(productId)
              .productQuantity(productQuantity)
              .payAmount(payAmount)
              .build();
    }
  }

}
