package com.ttip.mesa_agil.integration;

import com.ttip.mesa_agil.controller.MenuController;
import com.ttip.mesa_agil.dto.responses.MenuResponse;
import com.ttip.mesa_agil.service.MenuService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

@WebMvcTest(MenuController.class)
public class MenuControllerTest {

    @Autowired
    MockMvcTester mvc;

    @MockitoBean
    MenuService menuService;

    @Test
    void getMenu() {

        MenuResponse response = new MenuResponse(
                List.of(),
                ""
        );

        when(menuService.getMenu())
                .thenReturn(response);

        mvc.get()
                .uri("/menu")
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.OK);

        verify(menuService).getMenu();
    }
}
