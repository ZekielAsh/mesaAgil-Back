package com.ttip.mesa_agil.repository;

import com.ttip.mesa_agil.model.FoodCategory;
import com.ttip.mesa_agil.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    boolean existsByFoodCategory(FoodCategory foodCategory);

    List<Item> findByActiveTrue();

}