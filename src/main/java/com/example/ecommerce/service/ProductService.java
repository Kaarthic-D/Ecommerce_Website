package com.example.ecommerce.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.ecommerce.dto.ProductRequest;
import com.example.ecommerce.dto.ProductResponse;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository) {

        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public ProductResponse createProduct(
            ProductRequest request) {

        Category category =
                categoryRepository.findById(
                        request.getCategoryId()
                ).orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Category not found"
                    )
                );

        Product product = new Product(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getStock(),
                category
        );

        Product savedProduct =
                productRepository.save(product);

        return convertToResponse(savedProduct);
    }

    public ProductResponse getProductById(Long id) {

        Product product =
                productRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Product not found with id: " + id
                    )
                );

        return convertToResponse(product);
    }

    public Page<ProductResponse> getProducts(
            String name,
            Long categoryId,
            Pageable pageable) {

        Page<Product> products;

        if (name != null && categoryId != null) {

            products =
                productRepository
                    .findByNameContainingIgnoreCaseAndCategoryId(
                        name,
                        categoryId,
                        pageable
                    );

        } else if (name != null) {

            products =
                productRepository
                    .findByNameContainingIgnoreCase(
                        name,
                        pageable
                    );

        } else if (categoryId != null) {

            products =
                productRepository
                    .findByCategoryId(
                        categoryId,
                        pageable
                    );

        } else {

            products =
                productRepository.findAll(pageable);
        }

        return products.map(this::convertToResponse);
    }

    public ProductResponse updateProduct(
            Long id,
            ProductRequest request) {

        Product product =
                productRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Product not found"
                    )
                );

        Category category =
                categoryRepository.findById(
                        request.getCategoryId()
                ).orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Category not found"
                    )
                );

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(category);

        Product updated =
                productRepository.save(product);

        return convertToResponse(updated);
    }

    public void deleteProduct(Long id) {

        Product product =
                productRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Product not found"
                    )
                );

        productRepository.delete(product);
    }

    private ProductResponse convertToResponse(
            Product product) {

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCategory().getId(),
                product.getCategory().getName()
        );
    }
}