package com.ttip.mesa_agil.service;

import com.ttip.mesa_agil.dto.responses.UserResponse;
import com.ttip.mesa_agil.exception.BusinessException;
import com.ttip.mesa_agil.mapper.UserMapper;
import com.ttip.mesa_agil.model.User;
import com.ttip.mesa_agil.model.enums.UserRole;
import com.ttip.mesa_agil.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new BusinessException("Username is required");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new BusinessException("Password too short");
        }
    }

    private void validateEmployeeRole(UserRole role) {
        if (role == null) {
            throw new BusinessException("Role is required");
        }

        if (role != UserRole.STAFF && role != UserRole.KITCHEN) {
            throw new BusinessException("Only STAFF and KITCHEN roles are allowed.");
        }
    }

    private void createUser(String username, String password, UserRole userRole) {
        validateUsername(username);
        validatePassword(password);

        if (this.existsByUsername(username)) {
            throw new BusinessException("User already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(userRole);

        userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional
    public void createIfNotExists(String username, String password, UserRole role) {
        if (userRepository.existsByUsername(username)) {
            return;
        }
        createUser(username, password, role);
    }

    public void createEmployee(String username, String password, UserRole role) {
        validateEmployeeRole(role);

        createUser(username, password, role);
    }

    @Transactional
    public void updateUserEmployee(Long id, String username) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found"));
        validateEmployeeRole(user.getRole());
        validateUsername(username);

        if (!user.getUsername().equals(username)
                && userRepository.existsByUsername(username)) {
            throw new BusinessException("User already exists");
        }

        user.setUsername(username);
    }

    @Transactional
    public void resetPasswordEmployee(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found"));

        validatePassword(newPassword);
        validateEmployeeRole(user.getRole());

        user.setPassword(passwordEncoder.encode(newPassword));
    }

    @Transactional
    public void deleteUserEmployee(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found"));

        validateEmployeeRole(user.getRole());

        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getEmployees() {
        List<User> userList = userRepository.findByRoleIn(
                List.of(UserRole.STAFF, UserRole.KITCHEN)
        );

        return UserMapper.toResponseList(userList);
    }
}
