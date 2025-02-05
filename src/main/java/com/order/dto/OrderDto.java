package com.order.dto;

import lombok.Data;

@Data
public class OrderDto {

    private String orderId;
    private String productId;
    private int quantity;
    private double price;
    private String status;  // Pending, Paid, Cancelled
}
