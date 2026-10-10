package com.example.ecommerce.dto;

public class CartItemResponse {

    private Long itemId;
    private Long productId;
    private String productName;
    private double price;
    private int quantity;
    private double subtotal;

    public CartItemResponse(
            Long itemId,
            Long productId,
            String productName,
            double price,
            int quantity,
            double subtotal) {

        this.itemId = itemId;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public Long getItemId() {
        return itemId;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return subtotal;
    }
}