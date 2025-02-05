package com.order.repository;

import com.order.entity.OrderEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface OrderRepo extends ReactiveMongoRepository<OrderEntity, String> {
}
