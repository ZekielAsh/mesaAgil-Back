package com.ttip.mesa_agil.repository;

import com.ttip.mesa_agil.model.FoodCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodCategoryRepository extends JpaRepository<FoodCategory, Long> {
    boolean existsByNameIgnoreCase(String name);
}
