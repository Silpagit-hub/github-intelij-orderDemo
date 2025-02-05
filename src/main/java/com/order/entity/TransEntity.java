package com.order.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "transaction")
@Data
public class TransEntity {

    @Id
    private String transId;
    private String orderId;
    private double amount;
    private String paymentStatus; // fail or pass
}
