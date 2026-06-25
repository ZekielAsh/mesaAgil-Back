package com.ttip.mesa_agil.service;

import com.ttip.mesa_agil.dto.requests.CreateRestaurantTableRequest;
import com.ttip.mesa_agil.dto.requests.UpdateRestaurantTableRequest;
import com.ttip.mesa_agil.dto.responses.RestaurantTableQrResponse;
import com.ttip.mesa_agil.dto.responses.TableOccupancyResponse;
import com.ttip.mesa_agil.dto.responses.TableSessionResponse;
import com.ttip.mesa_agil.exception.BusinessException;
import com.ttip.mesa_agil.exception.OrderNotFoundException;
import com.ttip.mesa_agil.exception.ResourceNotFoundException;
import com.ttip.mesa_agil.exception.RestaurantTableAlreadyExistsException;
import com.ttip.mesa_agil.helper.TableAssignmentValidator;
import com.ttip.mesa_agil.mapper.RestaurantTableMapper;
import com.ttip.mesa_agil.model.RestaurantTable;
import com.ttip.mesa_agil.model.TableSession;
import com.ttip.mesa_agil.model.User;
import com.ttip.mesa_agil.model.enums.OrderStatus;
import com.ttip.mesa_agil.model.enums.TableStatus;
import com.ttip.mesa_agil.repository.RestaurantTableRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RestaurantTableService {

    private final RestaurantTableRepository restaurantTableRepository;
    private final TableSessionService tableSessionService;
    private final OrderService orderService;
    private final QrCodeService qrCodeService;
    private final TableAssignmentValidator tableAssignmentValidator;
    private final String scanUrlTemplate;
    private final String qrImageUrlTemplate;
    private final String frontendSessionUrlTemplate;

    public RestaurantTableService(RestaurantTableRepository restaurantTableRepository,
                                  TableSessionService tableSessionService,
                                  OrderService orderService,
                                  QrCodeService qrCodeService,
                                  TableAssignmentValidator tableAssignmentValidator,
                                  @Value("${app.qr.scan-url-template:http://localhost:8080/tables/qr/{qrToken}/redirect}") String scanUrlTemplate,
                                  @Value("${app.qr.image-url-template:http://localhost:8080/tables/qr/{qrToken}/image}") String qrImageUrlTemplate,
                                  @Value("${app.qr.frontend-session-url-template:http://localhost:8081/tables/{qrToken}/session}") String frontendSessionUrlTemplate) {
        this.restaurantTableRepository = restaurantTableRepository;
        this.tableSessionService = tableSessionService;
        this.orderService = orderService;
        this.qrCodeService = qrCodeService;
        this.tableAssignmentValidator = tableAssignmentValidator;
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
            return restaurantTableRepository.saveAndFlush(table);
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

    @Transactional
    public RestaurantTableQrResponse update(Long tableId, UpdateRestaurantTableRequest request) {
        RestaurantTable table = restaurantTableRepository.findById(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found"));

        if (restaurantTableRepository.existsByNumberAndIdNot(request.number(), tableId)) {
            throw new RestaurantTableAlreadyExistsException(request.number());
        }

        table.setNumber(request.number());

        try {
            return toQrResponse(restaurantTableRepository.saveAndFlush(table));
        } catch (DataIntegrityViolationException ex) {
            throw new RestaurantTableAlreadyExistsException(request.number());
        }
    }

    @Transactional
    public RestaurantTableQrResponse enable(Long tableId) {
        RestaurantTable table = restaurantTableRepository.findById(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found"));

        table.setEnabled(true);

        return toQrResponse(
                restaurantTableRepository.save(table)
        );
    }

    @Transactional
    public RestaurantTableQrResponse close(Long tableId) {

        RestaurantTable table = restaurantTableRepository.findById(tableId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Table not found"));

        if (tableSessionService.hasActiveSession(tableId)) {
            throw new BusinessException(
                    "Cannot close an occupied table"
            );
        }

        table.setEnabled(false);

        return toQrResponse(
                restaurantTableRepository.save(table)
        );
    }

    @Transactional(readOnly = true)
    public TableSessionResponse resolveSession(String qrToken) {

        RestaurantTable table = restaurantTableRepository
                .findByQrToken(qrToken)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Table QR token not found"
                        ));

        if (!table.isEnabled()) {
            return new TableSessionResponse(
                    table.getId(),
                    table.getNumber(),
                    false,
                    table.getQrToken(),
                    null,
                    null,
                    false
            );
        }

        return tableSessionService
                .findActiveSession(table.getId())
                .map(session -> {

                    Long orderId = orderService
                                    .getOpenOrderBySession(
                                            session.getId(),
                                            OrderStatus.OPEN
                                    ).getId();
                    return new TableSessionResponse(
                            table.getId(),
                            table.getNumber(),
                            true,
                            table.getQrToken(),
                            session.getId(),
                            orderId,
                            true
                    );
                })
                .orElseGet(() ->
                        new TableSessionResponse(
                                table.getId(),
                                table.getNumber(),
                                true,
                                table.getQrToken(),
                                null,
                                null,
                                false
                        )
                );
    }

    @Transactional
    public URI buildFrontendSessionUri(String qrToken) {
        TableSessionResponse session = resolveSession(qrToken);
        String url = frontendSessionUrlTemplate
                .replace("{qrToken}", session.qrToken())
                .replace("{tableId}", session.tableId().toString())
                .replace("{sessionId}",
                        session.sessionId() == null
                                ? ""
                                : session.sessionId().toString()
                );

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

    private RestaurantTableQrResponse toQrResponse(RestaurantTable table) {
        return new RestaurantTableQrResponse(
                table.getId(),
                table.getNumber(),
                table.isEnabled(),
                table.getQrToken(),
                buildScanUrl(table.getQrToken()),
                buildQrImageUrl(table.getQrToken())
        );
    }

    @Transactional(readOnly = true)
    public List<TableOccupancyResponse> getOccupancy() {

        List<RestaurantTable> tables =
                restaurantTableRepository.findAll();

        return tables.stream()
                .map(this::toOccupancyResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TableOccupancyResponse getOccupancyByTableId(Long tableId) {
        RestaurantTable table =
                restaurantTableRepository
                        .findById(tableId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Table not found"));
        return toOccupancyResponse(table);
    }

    @Transactional
    public TableOccupancyResponse assignToCurrentStaff(Long tableId) {

        User currentUser = tableAssignmentValidator.getCurrentUser();

        RestaurantTable table =
                restaurantTableRepository
                        .findById(tableId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Table not found"));

        if (!table.isEnabled()) {
            throw new BusinessException("Closed tables cannot be assigned");
        }

        if (table.getAssignedStaff() != null &&
                        table.getAssignedStaff().getId().equals(currentUser.getId())) {
            throw new BusinessException("You are already assigned to this table");
        }

        if (table.getAssignedStaff() != null) {
            throw new BusinessException("Table already assigned to another waiter");
        }

        table.setAssignedStaff(currentUser);

        return toOccupancyResponse(restaurantTableRepository.save(table));
    }

    @Transactional
    public TableOccupancyResponse unassignFromCurrentStaff(Long tableId) {

        User currentUser = tableAssignmentValidator.getCurrentUser();

        RestaurantTable table =
                restaurantTableRepository
                        .findById(tableId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Table not found"));

        if (table.getAssignedStaff() == null) {
            throw new BusinessException("Table is not assigned");
        }

        if (!table.getAssignedStaff().getId().equals(currentUser.getId())) {
            throw new BusinessException("You are not assigned to this table");
        }

        table.setAssignedStaff(null);

        return toOccupancyResponse(restaurantTableRepository.save(table));
    }

    @Transactional(readOnly = true)
    public List<TableOccupancyResponse> getAssignedTables() {

        User currentUser = tableAssignmentValidator.getCurrentUser();

        return restaurantTableRepository
                .findAllByAssignedStaffId(
                        currentUser.getId()
                )
                .stream()
                .map(this::toOccupancyResponse)
                .toList();
    }

    public TableOccupancyResponse toOccupancyResponse(RestaurantTable table) {

        Optional<TableSession> session = tableSessionService.findActiveSession(table.getId());

        TableStatus status;

        if (!table.isEnabled()) {
            status = TableStatus.CLOSED;
        } else if (session.isPresent()) {
            status = TableStatus.OCCUPIED;
        } else {
            status = TableStatus.FREE;
        }

        return new TableOccupancyResponse(
                table.getId(),
                table.getNumber(),
                status,
                session.map(TableSession::getCustomerCount).orElse(0),
                session.map(TableSession::getId).orElse(null),
                table.getAssignedStaff() != null
                        ? table.getAssignedStaff().getId()
                        : null,
                table.getAssignedStaff() != null
                        ? table.getAssignedStaff().getUsername()
                        : null
        );
    }
}
