package com.ttip.mesa_agil.service;

import com.ttip.mesa_agil.dto.MenuItemDTO;
import com.ttip.mesa_agil.dto.responses.MenuResponse;
import com.ttip.mesa_agil.exception.OrderNotFoundException;
import com.ttip.mesa_agil.exception.ResourceNotFoundException;
import com.ttip.mesa_agil.model.FoodCategory;
import com.ttip.mesa_agil.model.Item;
import com.ttip.mesa_agil.repository.FoodCategoryRepository;
import com.ttip.mesa_agil.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private FoodCategoryRepository foodCategoryRepository;

    public MenuService(MenuRepository menuRepository, FoodCategoryRepository foodCategoryRepository) {
        this.menuRepository = menuRepository;
        this.foodCategoryRepository = foodCategoryRepository;
    }

    public void createItem(String name, String description, String imageUrl, BigDecimal price, Long categoryId) {
        FoodCategory category = foodCategoryRepository.findById(categoryId).orElseThrow(
                () -> new ResourceNotFoundException("Category with id " + categoryId + " doesn't exist")
        );

        Item item = new Item(null, name, description, imageUrl, price, category);
        menuRepository.save(item);
    }

    public MenuResponse getMenu() {
        List<Item> items = menuRepository.findAll();

        if (items.isEmpty()) {
            return new MenuResponse(List.of(), "No hay comidas disponibles");
        }

        List<MenuItemDTO> menuItems = items.stream()
                .map(item -> new MenuItemDTO(
                        item.getId(),
                        item.getName(),
                        item.getDescription(),
                        item.getPrice(),
                        item.getImageUrl()
                ))
                .toList();

        return new MenuResponse(menuItems, null);
    }

    public Item getItemById(Long itemId) {
        return menuRepository.findById(itemId).orElseThrow(
                () -> new OrderNotFoundException(itemId)
        );
    }

    public boolean isEmpty() {
        return menuRepository.findAll().isEmpty();
    }

}
