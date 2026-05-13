package com.ttip.mesa_agil.service;

import com.ttip.mesa_agil.exception.OrderNotFoundException;
import com.ttip.mesa_agil.exception.ResourceNotFoundException;
import com.ttip.mesa_agil.model.FoodCategory;
import com.ttip.mesa_agil.repository.FoodCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodCategoryService {

    private final FoodCategoryRepository foodCategoryRepository;

    public static FoodCategory create(String name) {

        String normalizedName = name.trim();

        if (normalizedName.isBlank()) {
            throw new IllegalArgumentException("Category name is required");
        }

        if (foodCategoryRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new IllegalArgumentException("Category already exists");
        }

        FoodCategory category = new FoodCategory();
        category.setName(normalizedName);

        return foodCategoryRepository.save(category);
    }

    public static FoodCategory update(Long id, String name) {

        FoodCategory category = foodCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        String normalizedName = name.trim();

        if (normalizedName.isBlank()) {
            throw new IllegalArgumentException("Category name is required");
        }

        if (foodCategoryRepository.existsByNameIgnoreCase(normalizedName)
                && !category.getName().equalsIgnoreCase(normalizedName)) {

            throw new IllegalArgumentException("Category already exists");
        }

        category.setName(normalizedName);

        return foodCategoryRepository.save(category);
    }

    public static void delete(Long id) {

        FoodCategory category = foodCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        foodCategoryRepository.delete(category);
    }

    public static List<FoodCategory> findAll() {
        return foodCategoryRepository.findAll();
    }
}