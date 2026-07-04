package com.ttip.mesa_agil.dto.requests;

import com.ttip.mesa_agil.model.enums.UserRole;

public record CreateUserRequest(String username, String password, UserRole role) {
}
