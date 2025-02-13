package com.order.serviceImpl;

import com.order.dto.TransDto;
import com.order.exception.DatabaseException;
import com.order.repository.TransRepo;
import com.order.entity.TransEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class TransService {

    @Autowired
    private KafkaTemplate<String, List<String>> kafkaTemplate;
    @Autowired
    TransRepo transRepo;
    @Autowired
    ModelMapper mapper;

    public Mono<TransEntity> createTrans(TransDto transaction) {
        TransEntity transDao = mapper.map(transaction, TransEntity.class);
        return transRepo.save(transDao)
                .doOnError(e -> {
                    // Log error here
                    throw new DatabaseException("Failed to save transaction to the database.");
                })
                ;

    }
}
