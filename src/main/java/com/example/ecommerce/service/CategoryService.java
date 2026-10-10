package com.example.ecommerce.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(
            CategoryRepository categoryRepository) {

        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(String name) {

        if (categoryRepository.existsByName(name)) {
            throw new RuntimeException(
                    "Category already exists"
            );
        }

        Category category = new Category(name);

        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {

        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {

        return categoryRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Category not found with id: " + id
                    )
                );
    }

    public Category updateCategory(
            Long id,
            String name) {

        Category category = getCategoryById(id);

        category.setName(name);

        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {

        Category category = getCategoryById(id);

        categoryRepository.delete(category);
    }
}