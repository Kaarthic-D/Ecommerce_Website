package com.example.ecommerce.dto;

public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private double price;
    private int stock;
    private Long categoryId;
    private String categoryName;

    public ProductResponse(
            Long id,
            String name,
            String description,
            double price,
            int stock,
            Long categoryId,
            String categoryName) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }
}