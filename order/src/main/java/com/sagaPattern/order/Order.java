package com.sagaPattern.order;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@ToString
// 실제로 만들때는 enum을 사용하자
public class Order {
  // UUID: DB를 연결하여 실습하지 않기 때문에, UUID를 사용하여 Id의 값이 겹치지 않도록 랜덤으로 값을 넣어준다.
  private UUID orderId;
  private String userId; // 주문 유저
  private String orderStatus;
  private String errorType; // 에러 사유

  // 주문 취소
  public void cancelOrder(String receiveErrorType){
    orderStatus = "CANCELLED";
    errorType = receiveErrorType;
  }
}
