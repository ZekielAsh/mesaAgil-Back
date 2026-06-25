package com.ttip.mesa_agil.service;

import com.ttip.mesa_agil.exception.BusinessException;
import com.ttip.mesa_agil.model.User;
import com.ttip.mesa_agil.model.enums.UserRole;
import com.ttip.mesa_agil.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private void createUser(String username, String password, UserRole userRole) {
        if (username == null || username.isBlank()) {
            throw new BusinessException("Username is required");
        }
        if (password.length() < 8) {
            throw new BusinessException("Password too short");
        }

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
}
