package com.order.serviceImpl;

import com.order.dto.OrderDto;
import com.order.entity.OrderEntity;
import com.order.exception.DatabaseException;
import com.order.exception.OrderNotFoundException;
import com.order.repository.OrderRepo;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {

    @Autowired
    private KafkaTemplate<String, List<String>> kafkaTemplate;
    @Autowired
    OrderRepo orderRepo;
    @Autowired
    ModelMapper mapper;

    // Create Order
    public Mono<List<OrderEntity>> createOrder(List<OrderDto> order) {
        List<OrderEntity> orderDao = order.stream()
                .map(orders -> {
                    OrderEntity orderEntity = new OrderEntity();
                    orderEntity.setOrderId(orders.getOrderId());
                    orderEntity.setPrice(orders.getPrice());
                    orderEntity.setQuantity(orders.getQuantity());
                    orderEntity.setStatus(orders.getStatus());
                    orderEntity.setProductId(orders.getProductId());
                    return orderEntity;
                })
                .toList();
log.info("***************Order Received "+orderDao+"**************");
        Mono<List<OrderEntity>> savedOrderFlux = orderRepo.saveAll(orderDao).collectList()
                .doOnNext(savedOrder -> {
                    List<String> orderIds = savedOrder.stream()
                            .map(OrderEntity::getOrderId)
                            .collect(Collectors.toList());
                    log.info("**********List of order ids are "+orderIds+"************");
                    //String message = "Orders placed with IDs: " + orderIds;
                    kafkaTemplate.send("order-topic", orderIds);
                    log.info("*********Message sent to Kafka: " + orderIds+"***********");
                })
                .onErrorResume(e -> {
                    log.error("**********Error while saving order: " + e.getMessage()+"**********");
                    return Mono.error(new DatabaseException("Failed to create order due to a database error"));
                });

        savedOrderFlux.subscribe();
        return savedOrderFlux;
    }

    public Flux<OrderEntity> getAllOrder(){
        return orderRepo.findAll();
    }


    // Get Order by ID
    public Mono<OrderEntity> getOrderById(String id) {
        return orderRepo.findById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new OrderNotFoundException("Order not found with ID: " + id))));
    }
}