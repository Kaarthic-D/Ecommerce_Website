package com.example.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.ecommerce.dto.CartRequest;
import com.example.ecommerce.dto.CartResponse;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.CurrentUserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final CurrentUserService currentUserService;

    public CartController(
            CartService cartService,
            CurrentUserService currentUserService) {

        this.cartService = cartService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addToCart(
            @Valid @RequestBody CartRequest request) {

        Long userId = currentUserService.getCurrentUser().getId();

        return ResponseEntity.ok(
                cartService.addToCart(userId, request));
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {

        Long userId = currentUserService.getCurrentUser().getId();

        return ResponseEntity.ok(
                cartService.getCart(userId));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> updateQuantity(
            @PathVariable Long itemId,
            @RequestParam int quantity) {

        Long userId = currentUserService.getCurrentUser().getId();

        return ResponseEntity.ok(
                cartService.updateQuantity(
                        userId, itemId, quantity));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<String> removeItem(
            @PathVariable Long itemId) {

        Long userId = currentUserService.getCurrentUser().getId();

        cartService.removeItem(userId, itemId);

        return ResponseEntity.ok("Item removed from cart");
    }
}