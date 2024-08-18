package com.sagaPattern.payment;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Payment {
    private UUID paymentId;
    private String userId; // 구매 유저

    private String payAmount; // 결제 금액

    private String payStatus; // 결제 성공여부 저장
}