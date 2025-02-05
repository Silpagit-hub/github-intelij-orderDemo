package com.order.controller;

import com.order.dto.OrderDto;
import com.order.serviceImpl.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping("/create")
    public ResponseEntity<String> createOrder(@RequestBody OrderDto orderDto) {
        orderService.createOrder(orderDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("data saved succsefully");

    }
}
