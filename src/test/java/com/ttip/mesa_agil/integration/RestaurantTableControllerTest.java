package com.ttip.mesa_agil.integration;

import com.ttip.mesa_agil.controller.RestaurantTableController;
import com.ttip.mesa_agil.dto.requests.CreateRestaurantTableRequest;
import com.ttip.mesa_agil.dto.responses.RestaurantTableQrResponse;
import com.ttip.mesa_agil.dto.responses.TableSessionResponse;
import com.ttip.mesa_agil.exception.RestaurantTableAlreadyExistsException;
import com.ttip.mesa_agil.handler.GlobalExceptionHandler;
import com.ttip.mesa_agil.security.jwt.JwtAuthFilter;
import com.ttip.mesa_agil.service.RestaurantTableService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.net.URI;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@WebMvcTest(RestaurantTableController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
public class RestaurantTableControllerTest {

    @Autowired
    MockMvcTester mvc;

    @MockitoBean
    RestaurantTableService restaurantTableService;

    @MockitoBean
    JwtAuthFilter jwtAuthFilter;

    @Test
    void createTable() {
        when(restaurantTableService.createWithQrInfo(any(CreateRestaurantTableRequest.class)))
                .thenReturn(new RestaurantTableQrResponse(
                        1L,
                        7,
                        "qr-token",
                        "http://localhost:8080/tables/qr/qr-token/redirect",
                        "http://localhost:8080/tables/qr/qr-token/image"
                ));

        mvc.post()
                .uri("/tables")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "number": 7
                        }
                        """)
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .extractingPath("$.tableNumber")
                .isEqualTo(7);

        verify(restaurantTableService).createWithQrInfo(any(CreateRestaurantTableRequest.class));
    }

    @Test
    void createTableRequiresNumber() {
        mvc.post()
                .uri("/tables")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .extractingPath("$.message")
                .isEqualTo("Validation error");

        verifyNoInteractions(restaurantTableService);
    }

    @Test
    void createTableRejectsDuplicatedNumber() {
        when(restaurantTableService.createWithQrInfo(any(CreateRestaurantTableRequest.class)))
                .thenThrow(new RestaurantTableAlreadyExistsException(7));

        mvc.post()
                .uri("/tables")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "number": 7
                        }
                        """)
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.CONFLICT)
                .bodyJson()
                .extractingPath("$.message")
                .isEqualTo("The table already exists");
    }

    @Test
    void getQrImageInline() {
        when(restaurantTableService.getQrPng(1L))
                .thenReturn(new byte[] {1, 2, 3});

        mvc.get()
                .uri("/tables/1/qr")
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.OK)
                .hasContentType(MediaType.IMAGE_PNG);

        verify(restaurantTableService).getQrPng(1L);
    }

    @Test
    void getQrImageAsDownload() {
        when(restaurantTableService.getQrPng(1L))
                .thenReturn(new byte[] {1, 2, 3});

        mvc.get()
                .uri("/tables/1/qr?download=true")
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.OK)
                .hasHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"mesa-1-qr.png\"");
    }

    @Test
    void getQrInfo() {
        when(restaurantTableService.getQrInfo(1L))
                .thenReturn(new RestaurantTableQrResponse(
                        1L,
                        7,
                        "qr-token",
                        "http://localhost:8080/tables/qr/qr-token/redirect",
                        "http://localhost:8080/tables/qr/qr-token/image"
                ));

        mvc.get()
                .uri("/tables/1/qr-info")
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .extractingPath("$.qrToken")
                .isEqualTo("qr-token");

        verify(restaurantTableService).getQrInfo(1L);
    }

    @Test
    void getAllQrInfo() {
        when(restaurantTableService.getAllQrInfo())
                .thenReturn(List.of(new RestaurantTableQrResponse(
                        1L,
                        7,
                        "qr-token",
                        "http://localhost:8080/tables/qr/qr-token/redirect",
                        "http://localhost:8080/tables/qr/qr-token/image"
                )));

        mvc.get()
                .uri("/tables/qr-info")
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .extractingPath("$[0].qrToken")
                .isEqualTo("qr-token");

        verify(restaurantTableService).getAllQrInfo();
    }

    @Test
    void getPublicQrImageInline() {
        when(restaurantTableService.getQrPngByToken("qr-token"))
                .thenReturn(new byte[] {1, 2, 3});

        mvc.get()
                .uri("/tables/qr/qr-token/image")
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.OK)
                .hasContentType(MediaType.IMAGE_PNG);

        verify(restaurantTableService).getQrPngByToken("qr-token");
    }

    @Test
    void getPublicQrImageAsDownload() {
        when(restaurantTableService.getQrPngByToken("qr-token"))
                .thenReturn(new byte[] {1, 2, 3});

        mvc.get()
                .uri("/tables/qr/qr-token/image?download=true")
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.OK)
                .hasHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"mesa-qr.png\"");
    }

    @Test
    void resolveSession() {
        when(restaurantTableService.resolveSession("qr-token"))
                .thenReturn(new TableSessionResponse(
                        1L,
                        7,
                        "qr-token",
                        10L,
                        "OPEN",
                        true
                ));

        mvc.get()
                .uri("/tables/qr/qr-token/session")
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .extractingPath("$.orderId")
                .isEqualTo(10);

        verify(restaurantTableService).resolveSession("qr-token");
    }

    @Test
    void redirectToSession() {
        when(restaurantTableService.buildFrontendSessionUri("qr-token"))
                .thenReturn(URI.create("http://localhost:8081/tables/qr-token/session"));

        mvc.get()
                .uri("/tables/qr/qr-token/redirect")
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.FOUND)
                .hasHeader(HttpHeaders.LOCATION, "http://localhost:8081/tables/qr-token/session");

        verify(restaurantTableService).buildFrontendSessionUri("qr-token");
    }
}
