package com.order.controller;

import com.order.dto.OrderDto;
import com.order.serviceImpl.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/orders")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private KafkaTemplate<String, List<String>> kafkaTemplate;

    @PostMapping("/create")
    public ResponseEntity<String> createOrder(@RequestBody List<OrderDto> orderDto) {
        log.info("************* /create API called*************");
        orderService.createOrder(orderDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("data saved succsefully");

    }

//    @GetMapping("/getall")
//    public Mono<ResponseEntity<List<OrderDto>>> getOrders() {
//        return orderService.getAllOrder()  // Returns Flux<OrderEntity>
//                .map(order -> mapper.map(order, OrderDto.class)) // Convert each entity to DTO
//                .collectList() // Collect Flux into Mono<List<OrderDto>>
//                .map(orderList -> ResponseEntity.ok(orderList)); // Wrap in ResponseEntity with HTTP 200 OK
//    }

}
