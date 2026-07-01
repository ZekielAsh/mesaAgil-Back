package com.ttip.mesa_agil.repository;

import com.ttip.mesa_agil.dto.*;
import com.ttip.mesa_agil.model.Order;
import com.ttip.mesa_agil.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;


public interface StatsRepository extends JpaRepository<Order, Long> {

    @Query("""
    SELECT oi
    FROM OrderItem oi
    JOIN FETCH oi.order o
    WHERE o.status = 'CLOSED'
    AND o.createdAt BETWEEN :from AND :to
""")
    List<OrderItem> getRevenueTimelineData(
            Instant from,
            Instant to
    );

    @Query("""
        SELECT COALESCE(SUM(oi.unitPrice * oi.quantity), 0)
        FROM OrderItem oi
        WHERE oi.order.status = 'CLOSED'
        AND oi.order.createdAt BETWEEN :from AND :to
    """)
    BigDecimal getTotalRevenue(
            Instant from,
            Instant to
    );

    @Query("""
        SELECT COUNT(o)
        FROM Order o
        WHERE o.status = 'CLOSED'
        AND o.createdAt BETWEEN :from AND :to
    """)
    Long getTotalOrders(
            Instant from,
            Instant to
    );

    @Query("""
        SELECT new com.ttip.mesa_agil.dto.TopItemDto(
            oi.item.name,
            SUM(oi.quantity)
        )
        FROM OrderItem oi
        WHERE oi.order.status = 'CLOSED'
        AND oi.order.createdAt BETWEEN :from AND :to
        GROUP BY oi.item.name
        ORDER BY SUM(oi.quantity) DESC
    """)
    List<TopItemDto> getTopProducts(
            Instant from,
            Instant to
    );

    @Query("""
        SELECT new com.ttip.mesa_agil.dto.TopRevenueItemDto(
            oi.item.name,
            SUM(oi.unitPrice * oi.quantity)
        )
        FROM OrderItem oi
        WHERE oi.order.status = 'CLOSED'
        AND oi.order.createdAt BETWEEN :from AND :to
        GROUP BY oi.item.name
        ORDER BY SUM(oi.unitPrice * oi.quantity) DESC
    """)
    List<TopRevenueItemDto> getTopRevenueProducts(
            Instant from,
            Instant to
    );

    @Query("""
        SELECT new com.ttip.mesa_agil.dto.CategoryRevenueDto(
            oi.item.foodCategory.name,
            SUM(oi.unitPrice * oi.quantity)
        )
        FROM OrderItem oi
        WHERE oi.order.status = 'CLOSED'
        AND oi.order.createdAt BETWEEN :from AND :to
        GROUP BY oi.item.foodCategory.name
        ORDER BY SUM(oi.unitPrice * oi.quantity) DESC
    """)
    List<CategoryRevenueDto> getCategoryRevenue(
            Instant from,
            Instant to
    );

    @Query("""
        SELECT new com.ttip.mesa_agil.dto.TableOrdersDto(
            o.table.number,
            COUNT(o)
        )
        FROM Order o
        WHERE o.status = 'CLOSED'
        AND o.createdAt BETWEEN :from AND :to
        GROUP BY o.table.number
        ORDER BY COUNT(o) DESC
    """)
    List<TableOrdersDto> getTableOrders(
            Instant from,
            Instant to
    );

    @Query("""
        SELECT new com.ttip.mesa_agil.dto.TableRevenueDto(
            o.table.number,
            SUM(oi.unitPrice * oi.quantity)
        )
        FROM OrderItem oi
        JOIN oi.order o
        WHERE o.status = 'CLOSED'
        AND o.createdAt BETWEEN :from AND :to
        GROUP BY o.table.number
        ORDER BY SUM(oi.unitPrice * oi.quantity) DESC
    """)
    List<TableRevenueDto> getTableRevenue(
            Instant from,
            Instant to
    );
}
