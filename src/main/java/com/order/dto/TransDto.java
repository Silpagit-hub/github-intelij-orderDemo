package com.order.dto;

import lombok.Data;

@Data
public class TransDto {
    private String transId; // auto create
    private String orderId; // id = 121
    private double amount; //
    private String paymentStatus;
}
