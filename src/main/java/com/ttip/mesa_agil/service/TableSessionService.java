package com.ttip.mesa_agil.service;

import com.ttip.mesa_agil.dto.requests.CreateTableSessionRequest;
import com.ttip.mesa_agil.dto.requests.UpdateCustomerCountRequest;
import com.ttip.mesa_agil.dto.responses.TableSessionDetailsResponse;
import com.ttip.mesa_agil.exception.BusinessException;
import com.ttip.mesa_agil.exception.ResourceNotFoundException;
import com.ttip.mesa_agil.helper.TableAssignmentValidator;
import com.ttip.mesa_agil.mapper.TableSessionMapper;
import com.ttip.mesa_agil.model.Order;
import com.ttip.mesa_agil.model.RestaurantTable;
import com.ttip.mesa_agil.model.TableSession;
import com.ttip.mesa_agil.model.enums.OrderItemStatus;
import com.ttip.mesa_agil.model.enums.OrderStatus;
import com.ttip.mesa_agil.repository.OrderRepository;
import com.ttip.mesa_agil.repository.RestaurantTableRepository;
import com.ttip.mesa_agil.repository.TableSessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TableSessionService {

    private final TableSessionRepository tableSessionRepository;
    private final RestaurantTableRepository tableRepository;
    private final OrderRepository orderRepository;
    private final TableAssignmentValidator tableAssignmentValidator;

    @Transactional
    public TableSessionDetailsResponse createSession(Long tableId, CreateTableSessionRequest request) {
        RestaurantTable table =
                tableRepository.findById(tableId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Table not found"
                                ));

        tableAssignmentValidator.validateCurrentUserAssigned(tableId);

        if (!table.isEnabled()) {
            throw new BusinessException("Table is closed");
        }

        if (tableSessionRepository.existsByTableIdAndActiveTrue(tableId)) {
            throw new BusinessException("Table already occupied");
        }

        TableSession session = new TableSession();
        session.setTable(table);
        session.setCustomerCount(
                Optional.ofNullable(request.customerCount())
                        .orElse(0));

        TableSession savedSession = tableSessionRepository.save(session);

        Order order = new Order();

        order.setTable(table);
        order.setTableSession(savedSession);
        order.setStatus(OrderStatus.OPEN);

        orderRepository.save(order);

        return TableSessionMapper.toResponse(savedSession);
    }

    @Transactional
    public void closeSession(Long tableId) {
        tableAssignmentValidator.validateCurrentUserAssigned(tableId);

        TableSession session = findActiveSession(tableId).orElseThrow(
                () -> new BusinessException("Session is already closed"));

        Order order = orderRepository
                .findByTableSessionIdAndStatus(session.getId(), OrderStatus.OPEN)
                .orElse(null);

        if (order != null) {
            order.setStatus(OrderStatus.CANCELLED);
            order.setBillRequested(false);
            order.setClosedAt(LocalDateTime.now());

            order.getItems()
                    .stream()
                    .filter(item ->
                            item.getStatus() != OrderItemStatus.DELIVERED)
                    .forEach(item ->
                            item.setStatus(OrderItemStatus.CANCELLED));
            orderRepository.save(order);
        }

        session.setActive(false);
        session.setEndedAt(LocalDateTime.now());

        tableSessionRepository.save(session);
    }

    public Optional<TableSession> findActiveSession(Long tableId) {
        return tableSessionRepository
                .findByTableIdAndActiveTrue(tableId);
    }

    public boolean hasActiveSession(Long tableId) {
        return tableSessionRepository.existsByTableIdAndActiveTrue(tableId);
    }

    public List<TableSession> getActiveSessions() {
        return tableSessionRepository.findAllByActiveTrue();
    }

    @Transactional
    public TableSessionDetailsResponse updateCustomerCount(
            Long sessionId,
            UpdateCustomerCountRequest request
    ) {

        TableSession session =
                tableSessionRepository.findById(sessionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Session not found"));

        if (!session.getActive()) {
            throw new BusinessException("Session is closed");
        }

        session.setCustomerCount(request.customerCount());

        return TableSessionMapper.toResponse(session);
    }
}
