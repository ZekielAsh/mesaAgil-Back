package com.ttip.mesa_agil.repository;

import com.ttip.mesa_agil.model.Order;
import com.ttip.mesa_agil.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    boolean existsByTableIdAndStatus(Long tableId, OrderStatus status);
}
