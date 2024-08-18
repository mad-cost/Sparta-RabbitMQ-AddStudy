package com.sagaPattern.order;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderApplicationQueueConfig {

  // Jackson 라이브러리 사용
  @Bean
  public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Value("${message.exchange}")
  private String exchange;

  @Value("${message.queue.product}")
  private String queueProduct;

  @Value("${message.queue.payment}")
  private String queuePayment;


  @Value("${message.err.exchange}")
  private String exchangeErr;

  @Value("${message.queue.err.order}")
  private String queueErrOrder;

  @Value("${message.queue.err.product}")
  private String queueErrProduct;

  @Bean // market exchange 생성
  public TopicExchange exchange() {
    return new TopicExchange(exchange);
  }

  @Bean // market.product 큐 생성
  public Queue queueProduct() {
    return new Queue(queueProduct);
  }
  @Bean // market.payment 큐 생성
  public Queue queuePayment() {
    return new Queue(queuePayment);
  }

  // 1. 어느 큐로 보낼지 / 2. 어떤 exchange와 연결할지 / 3. 바인딩 이름

  @Bean // market.product 큐에 market exchange 바인딩
  public Binding bindingProduct() {
    return BindingBuilder.bind(
            queueProduct())
            .to(exchange())
            .with(queueProduct); // 바인딩 이름 설정 (큐와 이름을 똑같이 만들어서 헷갈리지 않게 한다)
  }
  @Bean
  public Binding bindingPayment() {
    return BindingBuilder.bind(
            queuePayment())
            .to(exchange())
            .with(queuePayment);
  }

  @Bean // market.err exchange 생성
  public TopicExchange exchangeErr() {
    return new TopicExchange(exchangeErr);
  }

  @Bean
  public Queue queueErrOrder() {
    return new Queue(queueErrOrder);
  }
  @Bean
  public Queue queueErrProduct() {
    return new Queue(queueErrProduct);
  }

  @Bean
  public Binding bindingErrOrder() {
    return BindingBuilder.bind(queueErrOrder()).to(exchangeErr()).with(queueErrOrder);
  }
  @Bean
  public Binding bindingErrProduct() {
    return BindingBuilder.bind(queueErrProduct()).to(exchangeErr()).with(queueErrProduct);
  }
}
