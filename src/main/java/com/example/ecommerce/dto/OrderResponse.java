package com.example.ecommerce.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.ecommerce.entity.OrderStatus;

public class OrderResponse {

    private Long orderId;
    private double totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;

    public OrderResponse(
            Long orderId,
            double totalAmount,
            OrderStatus status,
            LocalDateTime createdAt,
            List<OrderItemResponse> items) {

        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.items = items;
    }

    public Long getOrderId() {
        return orderId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }
}