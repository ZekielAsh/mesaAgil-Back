package com.ttip.mesa_agil.model;

import lombok.Getter;
import java.util.List;

@Getter
public class Menu {

    private final List<Item> comidas;

    public Menu(List<Item> comidas) {
        this.comidas = comidas;
    }

    public boolean isEmpty() {
        return comidas.isEmpty();
    }
}
