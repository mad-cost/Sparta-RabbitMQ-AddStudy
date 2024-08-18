package com.sagaPattern.order;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryMessage {
  // UUID: DB를 연결하여 실습하지 않기 때문에, UUID를 사용하여 Id의 값이 겹치지 않도록 랜덤으로 값을 넣어준다.
  private UUID orderId;
  private UUID paymentId;

  private String userId; // 요청자

  private Integer productId; // 주문 상품
  private Integer productQuantity; // 주문 상품 수량

  private Integer payAmount; // 금액

  private String errorType; // 중간 로직에서 에러 발생시 에러 사유
}
