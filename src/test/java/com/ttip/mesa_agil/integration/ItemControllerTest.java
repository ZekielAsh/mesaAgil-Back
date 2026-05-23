package com.ttip.mesa_agil.integration;

import com.ttip.mesa_agil.controller.ItemController;
import com.ttip.mesa_agil.dto.requests.CreateItemRequest;
import com.ttip.mesa_agil.dto.requests.UpdateItemRequest;
import com.ttip.mesa_agil.model.FoodCategory;
import com.ttip.mesa_agil.model.Item;
import com.ttip.mesa_agil.security.jwt.JwtAuthFilter;
import com.ttip.mesa_agil.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ItemControllerTest {

    @Autowired
    MockMvcTester mvc;

    @MockitoBean
    ItemService itemService;

    @MockitoBean
    JwtAuthFilter jwtAuthFilter;

    @Test
    void createItem() {

        Item item = new Item();
        item.setId(1L);
        item.setName("Pizza");
        item.setPrice(BigDecimal.valueOf(1000));

        FoodCategory category = new FoodCategory();
        category.setName("Pizzas");

        item.setFoodCategory(category);

        when(itemService.create(any(CreateItemRequest.class)))
                .thenReturn(item);

        mvc.post()
                .uri("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "Pizza",
                            "price": 1000
                        }
                        """)
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .extractingPath("$.name")
                .isEqualTo("Pizza");

        verify(itemService)
                .create(any(CreateItemRequest.class));
    }

    @Test
    void updateItem() {

        Item item = new Item();
        item.setId(1L);
        item.setName("Pizza Especial");
        item.setPrice(BigDecimal.valueOf(1500));

        FoodCategory category = new FoodCategory();
        category.setName("Pizzas");

        item.setFoodCategory(category);

        when(itemService.update(eq(1L), any(UpdateItemRequest.class)))
                .thenReturn(item);

        mvc.put()
                .uri("/items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "Pizza Especial",
                            "price": 1500
                        }
                        """)
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .extractingPath("$.name")
                .isEqualTo("Pizza Especial");

        verify(itemService)
                .update(eq(1L), any(UpdateItemRequest.class));
    }

    @Test
    void deleteItem() {

        mvc.delete()
                .uri("/items/1")
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.NO_CONTENT);

        verify(itemService).delete(1L);
    }

    @Test
    void findAllItems() {

        Item pizza = new Item();
        pizza.setId(1L);
        pizza.setName("Pizza");
        pizza.setPrice(BigDecimal.valueOf(1000));

        FoodCategory categoryPizza = new FoodCategory();
        categoryPizza.setName("Pizzas");

        pizza.setFoodCategory(categoryPizza);

        Item burger = new Item();
        burger.setId(2L);
        burger.setName("Burger");
        burger.setPrice(BigDecimal.valueOf(2000));

        FoodCategory categoryBurger = new FoodCategory();
        categoryBurger.setName("Pizzas");

        burger.setFoodCategory(categoryBurger);

        when(itemService.findAll())
                .thenReturn(List.of(pizza, burger));

        mvc.get()
                .uri("/items")
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .extractingPath("$[0].name")
                .isEqualTo("Pizza");

        verify(itemService).findAll();
    }
}
