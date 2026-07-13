package com.ttip.mesa_agil.repository;

import com.ttip.mesa_agil.model.User;
import com.ttip.mesa_agil.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    List<User> findByRoleIn(Collection<UserRole> roles);

}
