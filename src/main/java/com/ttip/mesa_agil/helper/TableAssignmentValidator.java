package com.ttip.mesa_agil.helper;

import com.ttip.mesa_agil.exception.BusinessException;
import com.ttip.mesa_agil.exception.ResourceNotFoundException;
import com.ttip.mesa_agil.model.RestaurantTable;
import com.ttip.mesa_agil.model.User;
import com.ttip.mesa_agil.repository.RestaurantTableRepository;
import com.ttip.mesa_agil.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TableAssignmentValidator {

    private final RestaurantTableRepository tableRepository;
    private final UserRepository userRepository;

    public void validateCurrentUserAssigned(Long tableId) {
        getAssignedTable(tableId);
    }

    public User getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assert authentication != null;
        return userRepository
                .findByUsername(authentication.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
    }

    public RestaurantTable getAssignedTable(Long tableId) {
        User currentUser = getCurrentUser();

        return tableRepository
                .findByIdAndAssignedStaffId(
                        tableId,
                        currentUser.getId()
                )
                .orElseThrow(() ->
                        new BusinessException("You are not assigned to this table"));
    }
}
