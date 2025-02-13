package com.order.serviceImpl;

import com.order.dto.TransDto;
import com.order.entity.OrderEntity;
import com.order.entity.TransEntity;
import com.order.repository.OrderRepo;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

@Slf4j
@Service
public class TransServiceConsumer {

    @Autowired
    OrderService orderService;

    @Autowired
    TransService transService;

    @Autowired
    private ModelMapper mapper;


    @KafkaListener(topics = "order-topic", groupId = "order-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeOrders(List<String> orderIds) {
        log.info("***************Received Order IDs from Kafka: " + orderIds);

        orderIds.forEach(orderId ->
                orderService.getOrderById(orderId)
                        .flatMap(orderEntity -> {
                            if (paymentStatus().equals("true")) {
                                TransEntity transEntity = new TransEntity();
                                transEntity.setTransId(transIdGenerator());
                                transEntity.setOrderId(orderEntity.getOrderId());
                                transEntity.setPaymentStatus("Paid");
                                transEntity.setAmount(orderEntity.getPrice());

                                TransDto transDto = mapper.map(transEntity, TransDto.class);
                                return transService.createTrans(transDto)
                                        .doOnSuccess(transaction ->
                                                log.info("Payment success and Transaction created: " + transaction)
                                        )
                                        .thenReturn(orderEntity);
                            } else {
                                log.info("Payment pending for Order ID: " + orderId);
                                return Mono.just(orderEntity);
                            }
                        })
                        .doOnError(error ->
                                log.error("**********Error processing order ID " + orderId + ": " + error.getMessage()+"*************")
                        )
                        .subscribe()
        );
    }

    private String transIdGenerator() {
        String transId = UUID.randomUUID().toString();
        return "Trans" + transId;
    }

    private String paymentStatus() {
        // Randomly simulate payment status as "true" or "false"
        return String.valueOf(new Random().nextBoolean());
    }
}
