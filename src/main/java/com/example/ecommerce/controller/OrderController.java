package com.example.ecommerce.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.ecommerce.dto.OrderResponse;
import com.example.ecommerce.service.CurrentUserService;
import com.example.ecommerce.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final CurrentUserService currentUserService;

    public OrderController(
            OrderService orderService,
            CurrentUserService currentUserService) {

        this.orderService = orderService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout() {

        Long userId = currentUserService.getCurrentUser().getId();

        return ResponseEntity.ok(
                orderService.placeOrder(userId));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getUserOrders() {

        Long userId = currentUserService.getCurrentUser().getId();

        return ResponseEntity.ok(
                orderService.getUserOrders(userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(	
            @PathVariable Long orderId) {

        Long userId = currentUserService.getCurrentUser().getId();

        return ResponseEntity.ok(
                orderService.getOrderById(userId, orderId));
    }
}