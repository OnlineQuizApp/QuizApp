package com.example.project_module6.controller;

import com.example.project_module6.dto.UserFilterRequest;
import com.example.project_module6.model.Users;
import com.example.project_module6.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "*")
public class UserAdminController {
    @Autowired
    private IUserService userService;

    @GetMapping
    public ResponseEntity<?> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean status,
            @RequestParam(required = false) Boolean softDelete,
            @RequestParam(required = false) LocalDateTime createdAt) {

        try {
            UserFilterRequest filter = new UserFilterRequest();
            filter.setName(name);
            filter.setRole(role);
            filter.setStatus(status);
            filter.setSoftDelete(softDelete);
            filter.setCreatedAt(createdAt);

            Pageable pageable = PageRequest.of(page, size);
            Page<Users> users = userService.findUsers(filter, pageable);
            System.out.println("Received createdAt: " + createdAt);
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid filter parameters: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<Users> toggleStatus(@PathVariable int id) {
        Users updatedUser = userService.toggleUserStatus(id);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<Users> restoreUser(@PathVariable int id) {
        Users restoredUser = userService.restoreUser(id);
        return ResponseEntity.ok(restoredUser);
    }
}
