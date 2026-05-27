package com.ttip.mesa_agil.service;

import com.ttip.mesa_agil.dto.requests.CreateRestaurantTableRequest;
import com.ttip.mesa_agil.dto.responses.RestaurantTableQrResponse;
import com.ttip.mesa_agil.dto.responses.TableSessionResponse;
import com.ttip.mesa_agil.exception.RestaurantTableAlreadyExistsException;
import com.ttip.mesa_agil.model.Order;
import com.ttip.mesa_agil.model.RestaurantTable;
import com.ttip.mesa_agil.model.enums.OrderStatus;
import com.ttip.mesa_agil.repository.OrderRepository;
import com.ttip.mesa_agil.repository.RestaurantTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RestaurantTableServiceTest {

    @Mock
    RestaurantTableRepository restaurantTableRepository;

    @Mock
    OrderRepository orderRepository;

    @Mock
    QrCodeService qrCodeService;

    RestaurantTableService restaurantTableService;

    @BeforeEach
    void setUp() {
        restaurantTableService = new RestaurantTableService(
                restaurantTableRepository,
                orderRepository,
                qrCodeService,
                "http://localhost:8080/tables/qr/{qrToken}/redirect",
                "http://localhost:8080/tables/qr/{qrToken}/image",
                "http://localhost:8081/tables/{qrToken}/session"
        );
    }

    @Test
    void createGeneratesUniqueQrToken() {
        when(restaurantTableRepository.existsByNumber(5))
                .thenReturn(false);
        when(restaurantTableRepository.existsByQrToken(anyString()))
                .thenReturn(false);
        when(restaurantTableRepository.saveAndFlush(any(RestaurantTable.class)))
                .thenAnswer(invocation -> {
                    RestaurantTable table = invocation.getArgument(0);
                    table.setId(1L);
                    return table;
                });

        RestaurantTable table = restaurantTableService.create(new CreateRestaurantTableRequest(5));

        assertThat(table.getQrToken()).isNotBlank();
        verify(restaurantTableRepository).saveAndFlush(any(RestaurantTable.class));
    }

    @Test
    void createRejectsDuplicatedTableNumber() {
        when(restaurantTableRepository.existsByNumber(5))
                .thenReturn(true);

        assertThatThrownBy(() -> restaurantTableService.create(new CreateRestaurantTableRequest(5)))
                .isInstanceOf(RestaurantTableAlreadyExistsException.class);

        verify(restaurantTableRepository, never()).saveAndFlush(any(RestaurantTable.class));
    }

    @Test
    void getQrInfoKeepsExistingTokenAndBuildsScanUrl() {
        RestaurantTable table = new RestaurantTable(1L, 5, "existing-token");

        when(restaurantTableRepository.findById(1L))
                .thenReturn(Optional.of(table));

        RestaurantTableQrResponse response = restaurantTableService.getQrInfo(1L);

        assertThat(response.qrToken()).isEqualTo("existing-token");
        assertThat(response.scanUrl()).isEqualTo("http://localhost:8080/tables/qr/existing-token/redirect");
        assertThat(response.qrImageUrl()).isEqualTo("http://localhost:8080/tables/qr/existing-token/image");
    }

    @Test
    void getQrPngByTokenGeneratesQrFromScanUrl() {
        RestaurantTable table = new RestaurantTable(1L, 5, "existing-token");

        when(restaurantTableRepository.findByQrToken("existing-token"))
                .thenReturn(Optional.of(table));
        when(qrCodeService.generatePng("http://localhost:8080/tables/qr/existing-token/redirect"))
                .thenReturn(new byte[] {1, 2, 3});

        byte[] response = restaurantTableService.getQrPngByToken("existing-token");

        assertThat(response).containsExactly(1, 2, 3);
    }

    @Test
    void resolveSessionReturnsOpenOrderForTableQr() {
        RestaurantTable table = new RestaurantTable(1L, 5, "existing-token");
        Order order = new Order();
        order.setId(10L);
        order.setStatus(OrderStatus.OPEN);

        when(restaurantTableRepository.findByQrToken("existing-token"))
                .thenReturn(Optional.of(table));
        when(orderRepository.findFirstByTableIdAndStatusOrderByCreatedAtDesc(1L, OrderStatus.OPEN))
                .thenReturn(Optional.of(order));

        TableSessionResponse response = restaurantTableService.resolveSession("existing-token");

        assertThat(response.tableId()).isEqualTo(1L);
        assertThat(response.orderId()).isEqualTo(10L);
        assertThat(response.orderStatus()).isEqualTo("OPEN");
        assertThat(response.activeSession()).isTrue();
    }

    @Test
    void getAllQrInfoBackfillsMissingTokens() {
        RestaurantTable table = new RestaurantTable(1L, 5, null);

        when(restaurantTableRepository.findAll())
                .thenReturn(List.of(table));
        when(restaurantTableRepository.existsByQrToken(anyString()))
                .thenReturn(false);
        when(restaurantTableRepository.save(any(RestaurantTable.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<RestaurantTableQrResponse> response = restaurantTableService.getAllQrInfo();

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().qrToken()).isNotBlank();
        assertThat(response.getFirst().scanUrl()).contains("/tables/qr/");
        assertThat(response.getFirst().qrImageUrl()).contains("/image");
    }
}
