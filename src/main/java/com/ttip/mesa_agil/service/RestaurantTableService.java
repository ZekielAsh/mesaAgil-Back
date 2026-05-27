package com.ttip.mesa_agil.service;

import com.ttip.mesa_agil.dto.requests.CreateRestaurantTableRequest;
import com.ttip.mesa_agil.dto.responses.RestaurantTableQrResponse;
import com.ttip.mesa_agil.dto.responses.TableSessionResponse;
import com.ttip.mesa_agil.exception.OrderNotFoundException;
import com.ttip.mesa_agil.exception.ResourceNotFoundException;
import com.ttip.mesa_agil.exception.RestaurantTableAlreadyExistsException;
import com.ttip.mesa_agil.mapper.RestaurantTableMapper;
import com.ttip.mesa_agil.model.Order;
import com.ttip.mesa_agil.model.RestaurantTable;
import com.ttip.mesa_agil.model.enums.OrderStatus;
import com.ttip.mesa_agil.repository.OrderRepository;
import com.ttip.mesa_agil.repository.RestaurantTableRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Service
public class RestaurantTableService {

    private final RestaurantTableRepository restaurantTableRepository;
    private final OrderRepository orderRepository;
    private final QrCodeService qrCodeService;
    private final String scanUrlTemplate;
    private final String qrImageUrlTemplate;
    private final String frontendSessionUrlTemplate;

    public RestaurantTableService(RestaurantTableRepository restaurantTableRepository,
                                  OrderRepository orderRepository,
                                  QrCodeService qrCodeService,
                                  @Value("${app.qr.scan-url-template:http://localhost:8080/tables/qr/{qrToken}/redirect}") String scanUrlTemplate,
                                  @Value("${app.qr.image-url-template:http://localhost:8080/tables/qr/{qrToken}/image}") String qrImageUrlTemplate,
                                  @Value("${app.qr.frontend-session-url-template:http://localhost:8081/tables/{qrToken}/session}") String frontendSessionUrlTemplate) {
        this.restaurantTableRepository = restaurantTableRepository;
        this.orderRepository = orderRepository;
        this.qrCodeService = qrCodeService;
        this.scanUrlTemplate = scanUrlTemplate;
        this.qrImageUrlTemplate = qrImageUrlTemplate;
        this.frontendSessionUrlTemplate = frontendSessionUrlTemplate;
    }
    // TODO: Change to dto once we need to create tables.
    public RestaurantTable getTableById(Long tableId) {
        return restaurantTableRepository.findById(tableId).orElseThrow(
                () -> new OrderNotFoundException(tableId)
        );
    }

    @Transactional
    public RestaurantTable create(CreateRestaurantTableRequest createRestaurantTableRequest) {
        if (restaurantTableRepository.existsByNumber(createRestaurantTableRequest.number())) {
            throw new RestaurantTableAlreadyExistsException(createRestaurantTableRequest.number());
        }

        RestaurantTable table = RestaurantTableMapper.toEntity(createRestaurantTableRequest);
        table.setQrToken(generateUniqueQrToken());

        try {
            RestaurantTable savedTable = restaurantTableRepository.saveAndFlush(table);
            createInitialOpenOrder(savedTable);

            return savedTable;
        } catch (DataIntegrityViolationException ex) {
            throw new RestaurantTableAlreadyExistsException(createRestaurantTableRequest.number());
        }
    }

    @Transactional
    public RestaurantTableQrResponse createWithQrInfo(CreateRestaurantTableRequest createRestaurantTableRequest) {
        return toQrResponse(create(createRestaurantTableRequest));
    }

    @Transactional
    public RestaurantTableQrResponse getQrInfo(Long tableId) {
        RestaurantTable table = getTableById(tableId);

        return toQrResponse(ensureQrToken(table));
    }

    @Transactional
    public List<RestaurantTableQrResponse> getAllQrInfo() {
        return restaurantTableRepository.findAll()
                .stream()
                .map(this::ensureQrToken)
                .map(this::toQrResponse)
                .toList();
    }

    @Transactional
    public byte[] getQrPng(Long tableId) {
        RestaurantTableQrResponse qrInfo = getQrInfo(tableId);

        return qrCodeService.generatePng(qrInfo.scanUrl());
    }

    @Transactional(readOnly = true)
    public byte[] getQrPngByToken(String qrToken) {
        RestaurantTable table = restaurantTableRepository.findByQrToken(qrToken)
                .orElseThrow(() -> new ResourceNotFoundException("Table QR token not found"));

        return qrCodeService.generatePng(buildScanUrl(table.getQrToken()));
    }

    @Transactional(readOnly = true)
    public TableSessionResponse resolveSession(String qrToken) {
        RestaurantTable table = restaurantTableRepository.findByQrToken(qrToken)
                .orElseThrow(() -> new ResourceNotFoundException("Table QR token not found"));

        return orderRepository.findFirstByTableIdAndStatusOrderByCreatedAtDesc(table.getId(), OrderStatus.OPEN)
                .map(order -> toActiveSessionResponse(table, order))
                .orElseGet(() -> new TableSessionResponse(
                        table.getId(),
                        table.getNumber(),
                        table.getQrToken(),
                        null,
                        null,
                        false
                ));
    }

    public URI buildFrontendSessionUri(String qrToken) {
        TableSessionResponse session = resolveSession(qrToken);
        String url = frontendSessionUrlTemplate
                .replace("{qrToken}", session.qrToken())
                .replace("{tableId}", session.tableId().toString())
                .replace("{orderId}", session.orderId() == null ? "" : session.orderId().toString());

        return URI.create(url);
    }

    private RestaurantTable ensureQrToken(RestaurantTable table) {
        if (table.getQrToken() != null && !table.getQrToken().isBlank()) {
            return table;
        }

        table.setQrToken(generateUniqueQrToken());

        return restaurantTableRepository.save(table);
    }

    private String generateUniqueQrToken() {
        String token;

        do {
            token = UUID.randomUUID().toString();
        } while (restaurantTableRepository.existsByQrToken(token));

        return token;
    }

    private String buildScanUrl(String qrToken) {
        return scanUrlTemplate.replace("{qrToken}", qrToken);
    }

    private String buildQrImageUrl(String qrToken) {
        return qrImageUrlTemplate.replace("{qrToken}", qrToken);
    }

    private void createInitialOpenOrder(RestaurantTable table) {
        if (orderRepository.existsByTableIdAndStatus(table.getId(), OrderStatus.OPEN)) {
            return;
        }
        Order order = new Order();
        order.setTable(table);
        order.setStatus(OrderStatus.OPEN);

        orderRepository.save(order);
    }

    private RestaurantTableQrResponse toQrResponse(RestaurantTable table) {
        return new RestaurantTableQrResponse(
                table.getId(),
                table.getNumber(),
                table.getQrToken(),
                buildScanUrl(table.getQrToken()),
                buildQrImageUrl(table.getQrToken())
        );
    }

    private TableSessionResponse toActiveSessionResponse(RestaurantTable table, Order order) {
        return new TableSessionResponse(
                table.getId(),
                table.getNumber(),
                table.getQrToken(),
                order.getId(),
                order.getStatus().name(),
                true
        );
    }
}
