package com.ttip.mesa_agil.integration;

import com.ttip.mesa_agil.controller.FoodCategoryController;
import com.ttip.mesa_agil.dto.responses.CategoryResponse;
import com.ttip.mesa_agil.exception.CategoryNotEmptyException;
import com.ttip.mesa_agil.mapper.CategoryMapper;
import com.ttip.mesa_agil.model.FoodCategory;
import com.ttip.mesa_agil.service.FoodCategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.mockito.Mockito.*;

@WebMvcTest(controllers = { FoodCategoryController.class })
public class FoodCategoryControllerTest {

    @Autowired
    MockMvcTester mvc;

    @MockitoBean
    FoodCategoryService service;

    @MockitoBean
    CategoryMapper mapper;

    @Test
    void createFoodCategory() {

        FoodCategory category = new FoodCategory();
        category.setName("Pizza");

        CategoryResponse response =
                new CategoryResponse(1L, "Pizza", 1);

        when(service.create("Pizza"))
                .thenReturn(category);

        when(mapper.toResponse(category))
                .thenReturn(response);

        mvc.post()
                .uri("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "Pizza"
                        }
                        """)
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.CREATED);

        verify(service).create("Pizza");
    }

    @Test
    void updateFoodCategory() {
        FoodCategory category = new FoodCategory();
        category.setId(1L);
        category.setName("Pastas");

        CategoryResponse response =
                new CategoryResponse(1L, "Pastas", 1);

        when(service.update(1L, "Pastas"))
                .thenReturn(category);

        when(mapper.toResponse(category))
                .thenReturn(response);

        mvc.put()
                .uri("/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "Pastas"
                        }
                        """)
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .extractingPath("$.name")
                .isEqualTo("Pastas");

        verify(service).update(1L, "Pastas");
    }

    @Test
    void deleteFoodCategory() {

        mvc.delete()
                .uri("/categories/1")
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.NO_CONTENT);

        verify(service).delete(1L);
    }

    @Test
    void shouldReturn409WhenCategoryNonEmptyOnDelete() {

        doThrow(new CategoryNotEmptyException("Cannot delete a non empty category"))
                .when(service)
                .delete(1L);

        mvc.delete()
                .uri("/categories/1")
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.CONFLICT)
                .bodyJson()
                .extractingPath("$.message")
                .isEqualTo("Cannot delete a non empty category");
    }
}
