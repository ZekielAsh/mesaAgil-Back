package com.ttip.mesa_agil.service;

import com.ttip.mesa_agil.dto.MenuItemDTO;
import com.ttip.mesa_agil.dto.MenuResponse;
import com.ttip.mesa_agil.model.Item;
import com.ttip.mesa_agil.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public void createItem(String name, String description, String imageUrl, BigDecimal price) {
        Item item = new Item(null, name, description, imageUrl, price);
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

    public boolean isEmpty() {
        return menuRepository.findAll().isEmpty();
    }

}
