package com.example.orderservice.service;

import com.example.orderservice.dto.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedEventProducer {

    @Value("${spring.kafka.producer.topic}")
    private String topic;

    private final KafkaTemplate<Integer,OrderCreatedEvent> kafkaTemplate;

    public void produceOrderCreatedEvent(OrderCreatedEvent event){
       log.info("Order crearted: {}", event.getOrderId());
        kafkaTemplate.send(topic,event);


    }
}
