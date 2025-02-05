package com.order.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ErrorResponse {

    private LocalDateTime timestamp;
    private String message;
    private HttpStatus status;

    // Constructor to initialize all fields
    public ErrorResponse(LocalDateTime timestamp, String message, HttpStatus status) {
        this.timestamp = timestamp;
        this.message = message;
        this.status = status;
    }

    // Getters and Setters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
