package com.ttip.mesa_agil.integration;

import com.ttip.mesa_agil.controller.OrderController;
import com.ttip.mesa_agil.dto.requests.CreateOrderItemsRequest;
import com.ttip.mesa_agil.dto.requests.CreateOrderRequest;
import com.ttip.mesa_agil.dto.responses.ItemResponse;
import com.ttip.mesa_agil.dto.responses.OrderItemResponse;
import com.ttip.mesa_agil.dto.responses.OrderResponse;
import com.ttip.mesa_agil.exception.OrderClosedException;
import com.ttip.mesa_agil.exception.OrderNotFoundException;
import com.ttip.mesa_agil.exception.TableAlreadyHasOpenOrderException;
import com.ttip.mesa_agil.handler.GlobalExceptionHandler;
import com.ttip.mesa_agil.model.enums.OrderItemStatus;
import com.ttip.mesa_agil.security.jwt.JwtAuthFilter;
import com.ttip.mesa_agil.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
public class OrderControllerTest {

    @Autowired
    MockMvcTester mvc;

    @MockitoBean
    OrderService orderService;

    @MockitoBean
    JwtAuthFilter jwtAuthFilter;

    @Test
    void getOrderById() {
        OrderItemResponse orderItem = new OrderItemResponse(
                1L,
                1L,
                1,
                new ItemResponse(
                        1L,
                        "Pizza",
                        "description",
                        "imageUrl",
                        BigDecimal.valueOf(500),
                        "category",
                        false
                ),
                2,
                BigDecimal.valueOf(1000),
                OrderItemStatus.PENDING,
                LocalDateTime.now()
        );

        OrderResponse response = new OrderResponse(
                1L,
                10L,
                List.of(orderItem),
                "OPEN",
                LocalDateTime.now(),
                null
        );

        when(orderService.getOrderById(1L))
                .thenReturn(response);

        mvc.get()
                .uri("/orders/1")
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.OK);

        verify(orderService).getOrderById(1L);
    }

    @Test
    void closeOrder() {

        mvc.post()
                .uri("/orders/1/close")
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.NO_CONTENT);

        verify(orderService).closeOrderById(1L);
    }

    @Test
    void createOrder() {

        OrderItemResponse orderItem = new OrderItemResponse(
                1L,
                1L,
                1,
                new ItemResponse(
                        1L,
                        "Pizza",
                        "description",
                        "imageUrl",
                        BigDecimal.valueOf(500),
                        "category",
                        false
                ),
                2,
                BigDecimal.valueOf(1000),
                OrderItemStatus.PENDING,
                LocalDateTime.now()
        );

        OrderResponse response = new OrderResponse(
                1L,
                10L,
                List.of(orderItem),
                "OPEN",
                LocalDateTime.now(),
                null
        );

        when(orderService.create(any(CreateOrderRequest.class)))
                .thenReturn(response);

        mvc.post()
                .uri("/orders/table/5")
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.OK);

        verify(orderService)
                .create(any(CreateOrderRequest.class));
    }

    @Test
    void addItemsToOrder() {

        OrderItemResponse orderItem = new OrderItemResponse(
                1L,
                1L,
                1,
                new ItemResponse(
                        1L,
                        "Pizza",
                        "description",
                        "imageUrl",
                        BigDecimal.valueOf(500),
                        "category",
                        false
                ),
                2,
                BigDecimal.valueOf(1000),
                OrderItemStatus.PENDING,
                LocalDateTime.now()
        );

        OrderResponse response = new OrderResponse(
                1L,
                10L,
                List.of(orderItem),
                "OPEN",
                LocalDateTime.now(),
                null
        );

        when(orderService.addItems(
                eq(1L),
                any(CreateOrderItemsRequest.class)
        )).thenReturn(response);

        mvc.post()
                .uri("/orders/1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "items": [
                                {
                                    "itemId": 1,
                                    "quantity": 2
                                }
                            ]
                        }
                        """)
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.OK);

        verify(orderService)
                .addItems(eq(1L), any(CreateOrderItemsRequest.class));
    }

    @Test
    void shouldReturn404WhenOrderNotFound() {

        when(orderService.getOrderById(1L))
                .thenThrow(new OrderNotFoundException(1L));

        mvc.get()
                .uri("/orders/1")
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.NOT_FOUND)
                .bodyJson()
                .extractingPath("$.message")
                .isEqualTo("Resource not found");
    }

    @Test
    void shouldReturn409WhenTableAlreadyHasOpenOrder() {

        when(orderService.create(any()))
                .thenThrow(
                        new TableAlreadyHasOpenOrderException(1L)
                );

        mvc.post()
                .uri("/orders/table/1")
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.CONFLICT)
                .bodyJson()
                .extractingPath("$.message")
                .isEqualTo("The table already has an open order");
    }

    @Test
    void shouldReturn409WhenOrderClosed() {

        when(orderService.addItems(eq(1L), any()))
                .thenThrow(new OrderClosedException(1L));

        mvc.post()
                .uri("/orders/1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "items": []
                    }
                    """)
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.CONFLICT)
                .bodyJson()
                .extractingPath("$.message")
                .isEqualTo("Cannot modify a closed order");
    }
}
