package com.ttip.mesa_agil.controller;

import com.ttip.mesa_agil.dto.responses.CategoryResponse;
import com.ttip.mesa_agil.dto.requests.CreateCategoryRequest;
import com.ttip.mesa_agil.mapper.CategoryMapper;
import com.ttip.mesa_agil.model.FoodCategory;
import com.ttip.mesa_agil.service.FoodCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class FoodCategoryController {

    private final FoodCategoryService foodCategoryService;
    private final CategoryMapper foodCategoryMapper;

    @PostMapping
    public ResponseEntity<CategoryResponse> create(
            @RequestBody CreateCategoryRequest request) {

        FoodCategory foodCategory = foodCategoryService.create(request.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(foodCategoryMapper.toResponse(foodCategory));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable Long id,
            @RequestBody CreateCategoryRequest request) {

        FoodCategory FoodCategory = foodCategoryService.update(id, request.getName());

        return ResponseEntity.ok(
                foodCategoryMapper.toResponse(FoodCategory)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        foodCategoryService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> findAll() {

        List<CategoryResponse> response = foodCategoryService.findAll()
                .stream()
                .map(foodCategoryMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }
}
