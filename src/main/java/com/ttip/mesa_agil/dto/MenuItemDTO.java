package com.ttip.mesa_agil.dto;

import lombok.Getter;

import java.math.BigDecimal;

public class MenuItemDTO {

    @Getter
    private final Long id;
    @Getter
    private final String name;
    @Getter
    private final String description;
    @Getter
    private final BigDecimal price;
    @Getter
    private final String imageurl;

    public MenuItemDTO(Long id, String name, String description, BigDecimal price, String imageurl) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.price = price;
    this.imageurl = imageurl;
    }

}