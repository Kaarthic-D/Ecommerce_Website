package com.example.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ecommerce.entity.Product;

public interface ProductRepository
        extends JpaRepository<Product, Long> {

    Page<Product> findByNameContainingIgnoreCase(
            String name,
            Pageable pageable);

    Page<Product> findByCategoryId(
            Long categoryId,
            Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseAndCategoryId(
            String name,
            Long categoryId,
            Pageable pageable);
}