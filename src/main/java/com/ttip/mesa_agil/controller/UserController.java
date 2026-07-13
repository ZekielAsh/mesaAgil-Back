package com.ttip.mesa_agil.controller;

import com.ttip.mesa_agil.dto.requests.CreateUserRequest;
import com.ttip.mesa_agil.dto.requests.ResetPasswordRequest;
import com.ttip.mesa_agil.dto.requests.UpdateUserRequest;
import com.ttip.mesa_agil.dto.responses.UserResponse;
import com.ttip.mesa_agil.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<Void> createEmployee(@RequestBody CreateUserRequest request) {
        userService.createEmployee(request.username(), request.password(), request.role());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateEmployee(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request) {
        userService.updateUserEmployee(id, request.username());

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        userService.deleteUserEmployee(id);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/password")
    public ResponseEntity<Void> resetPassword(
            @PathVariable Long id,
            @RequestBody ResetPasswordRequest request) {
        userService.resetPasswordEmployee(id, request.password());

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getEmployees() {
        return ResponseEntity.ok().body(userService.getEmployees());
    }

}
