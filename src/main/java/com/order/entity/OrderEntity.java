package com.order.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "Order")
@Data
public class OrderEntity {

    @Id
    private String orderId;
    private String productId;
    private int quantity;
    private double price;
    private String status;  // Pending, Paid, Cancelled
}
