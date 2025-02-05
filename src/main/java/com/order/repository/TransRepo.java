package com.order.repository;

import com.order.entity.TransEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TransRepo extends ReactiveMongoRepository<TransEntity, String> {
}
