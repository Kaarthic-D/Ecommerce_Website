package com.example.ecommerce.dto;

import java.util.List;

public class CartResponse {

    private Long cartId;
    private List<CartItemResponse> items;
    private double total;

    public CartResponse(
            Long cartId,
            List<CartItemResponse> items,
            double total) {

        this.cartId = cartId;
        this.items = items;
        this.total = total;
    }

    public Long getCartId() {
        return cartId;
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public double getTotal() {
        return total;
    }
}