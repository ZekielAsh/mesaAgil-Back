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
        User currentUser = getCurrentUser();

        RestaurantTable table =
                tableRepository.findById(tableId)
                        .orElseThrow(() -> new ResourceNotFoundException("Table not found"));

        if (table.getAssignedStaff() == null) {
            throw new BusinessException("Table has no assigned staff");
        }

        if (!tableRepository.existsByIdAndAssignedStaffId(tableId, currentUser.getId())) {
            throw new BusinessException("You are not assigned to this table");
        }
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
}
