package com.ttip.mesa_agil.repository;

import com.ttip.mesa_agil.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MenuRepository extends JpaRepository<Item, Long> {

    //En preparación a tener menus de entradas, bebidas y/o postres
    //List<Item> findByCategory(String category);
}
