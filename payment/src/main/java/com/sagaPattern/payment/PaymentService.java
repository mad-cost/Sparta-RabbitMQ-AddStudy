package com.sagaPattern.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${message.queue.err.product}")
    private String productErrorQueue;

    public void createPayment(DeliveryMessage deliveryMessage) {
        // Payment 객체 생성
        Payment payment = Payment.builder()
                .paymentId(UUID.randomUUID())
                .userId(deliveryMessage.getUserId())
                .payStatus("SUCCESS")
                .build();

        Integer payAmount = deliveryMessage.getPayAmount();

        // 결제 금액이 10000원보다 크면 에러 발생
        if (payAmount >= 10000) {
            log.error("Payment amount exceeds limit: {}", payAmount);
            payment.setPayStatus("CANCEL");
            deliveryMessage.setErrorType("PAYMENT_LIMIT_EXCEEDED");
            this.rollbackPayment(deliveryMessage);
        }
    }

    public void rollbackPayment(DeliveryMessage deliveryMessage) {
        log.info("PAYMENT ROLLBACK !!!");
        // market.err.product 큐로 에러 메시지 발송
        rabbitTemplate.convertAndSend(productErrorQueue, deliveryMessage);
    }
}