package com.order.serviceImpl;

import com.order.dto.OrderDto;
import com.order.entity.OrderEntity;
import com.order.exception.DatabaseException;
import com.order.exception.OrderNotFoundException;
import com.order.repository.OrderRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class OrderService {

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    OrderRepo orderRepo;
    @Autowired
    ModelMapper mapper;

    // Create Order
    public Mono<OrderEntity> createOrder(OrderDto order) {
        OrderEntity orderDao = mapper.map(order, OrderEntity.class);

        Mono<OrderEntity> savedOrderMono = orderRepo.save(orderDao)
                .doOnSuccess(savedOrder -> {
                    String message = "Order placed with ID: " + savedOrder.getOrderId();
                    kafkaTemplate.send("order-topic", message);
                    System.out.println("Message sent to Kafka: " + message);
                })
                .onErrorResume(e -> {
                    // Here we can handle database-specific exceptions, e.g. constraint violation
                    System.err.println("Error while saving order: " + e.getMessage());
                    return Mono.error(new DatabaseException("Failed to create order due to a database error"));
                });

        savedOrderMono.subscribe();
        return savedOrderMono;
    }


    // Get Order by ID
    public Mono<OrderEntity> getOrderById(String id) {
        return orderRepo.findById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new OrderNotFoundException("Order not found with ID: " + id))));
    }
}