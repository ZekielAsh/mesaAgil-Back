package com.ttip.mesa_agil.repository;

import com.ttip.mesa_agil.model.TableSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TableSessionRepository extends JpaRepository<TableSession, Long> {

    Optional<TableSession> findByTableIdAndActiveTrue(Long tableId);

    boolean existsByTableIdAndActiveTrue(Long tableId);

    List<TableSession> findAllByActiveTrue();
}