package com.ttip.mesa_agil.mapper;

import com.ttip.mesa_agil.dto.responses.UserResponse;
import com.ttip.mesa_agil.model.User;

import java.util.List;

public class UserMapper {

    public static UserResponse toResponse(User user) {
        return new UserResponse(user.getUsername(), user.getRole());
    }

    public static List<UserResponse> toResponseList(List<User> userList) {
        return userList.stream()
                .map(UserMapper::toResponse)
                .toList();
    }
}
