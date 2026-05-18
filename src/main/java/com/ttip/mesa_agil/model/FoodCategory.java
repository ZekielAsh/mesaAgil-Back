package com.ttip.mesa_agil.model;

import com.ttip.mesa_agil.service.MenuService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "t_food_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "foodCategory", cascade = CascadeType.ALL)
    private List<Item> comidas = new ArrayList<>();

    public void addItem(Item item) {
        comidas.add(item);
        item.setFoodCategory(this);
    }

    public void removeItem(Item item) {
        comidas.remove(item);
        item.setFoodCategory(null);
    }
}
