package com.ttip.mesa_agil.dto.responses;

import com.ttip.mesa_agil.model.enums.UserRole;

public record UserResponse(String username, UserRole role) {
}
