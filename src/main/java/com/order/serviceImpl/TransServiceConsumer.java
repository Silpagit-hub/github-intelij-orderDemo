package com.order.serviceImpl;

import com.order.dto.TransDto;
import com.order.entity.OrderEntity;
import com.order.entity.TransEntity;
import com.order.repository.OrderRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Random;
import java.util.UUID;

@Service
public class TransServiceConsumer {

    @Autowired
    OrderService orderService;

    @Autowired
    TransService transService;

    @Autowired
    private ModelMapper mapper;

    @KafkaListener(topics = "order-topic", groupId = "order-group")
    private void transListener(String msg) { // Order placed with ID: 121
        String[] sp = msg.split(" ");
        String orderId = sp[sp.length - 1];

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
                                .doOnSuccess(transaction -> {
                                    System.out.println("Transaction created: " + transaction);
                                })
                                .thenReturn(orderEntity);
                    } else {
                        System.out.println("The payment status is still pending");
                        return Mono.just(orderEntity);
                    }
                })
                .subscribe();
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
