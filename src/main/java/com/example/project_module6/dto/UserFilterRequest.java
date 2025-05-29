package com.example.project_module6.dto;

import com.example.project_module6.model.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFilterRequest {
    private String name;
    private String role;
    private Boolean status;
    private Boolean softDelete;
    private LocalDateTime createdAt=LocalDateTime.now();

    public Users.Role getRoleEnum() {
        if (role == null || role.isEmpty()) {
            return null;
        }
        try {
            return Users.Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Giá trị vai trò không hợp lệ: " + role + ". Các giá trị hợp lệ: " + java.util.Arrays.toString(Users.Role.values()));
        }
    }
}
