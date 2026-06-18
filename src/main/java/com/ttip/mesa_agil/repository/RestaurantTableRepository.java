package com.ttip.mesa_agil.repository;

import com.ttip.mesa_agil.model.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
    boolean existsByNumber(Integer number);

    boolean existsByNumberAndIdNot(Integer number, Long id);

    boolean existsByQrToken(String qrToken);

    Optional<RestaurantTable> findByQrToken(String qrToken);

    List<RestaurantTable> findAllByAssignedStaffId(Long staffId);
}
