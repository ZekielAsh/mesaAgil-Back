package com.ttip.mesa_agil.dto;

import java.util.List;

public class MenuResponse {
    private List<MenuItemDTO> items;
    private String message;

    public MenuResponse(List<MenuItemDTO> items, String message) {
        this.items = items;
        this.message = message;
    }

    public List<MenuItemDTO> getItems() {
        return items;
    }

    public String getMessage() {
        return message;
    }
}
