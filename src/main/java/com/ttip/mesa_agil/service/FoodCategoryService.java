package com.ttip.mesa_agil.service;

import com.ttip.mesa_agil.exception.CategoryNotEmptyException;
import com.ttip.mesa_agil.exception.ResourceNotFoundException;
import com.ttip.mesa_agil.model.FoodCategory;
import com.ttip.mesa_agil.repository.FoodCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FoodCategoryService {

    private final FoodCategoryRepository foodCategoryRepository;

    public FoodCategory create(String name) {

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

    public FoodCategory update(Long id, String name) {

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

    public void delete(Long id) {

        FoodCategory category =
                foodCategoryRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Category not found"
                                ));

        if (!category.getComidas().isEmpty()) {
            throw new CategoryNotEmptyException("Cannot delete a non empty category");
        }

        foodCategoryRepository.delete(category);
    }

    public List<FoodCategory> findAll() {
        return foodCategoryRepository.findAll();
    }
}