package com.example.project_module6.dto;

import com.example.project_module6.model.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private int id;
    private String username;
    private String password;
    private String passwordConfirm;
    private String role = "USER";
    private String name;
    private String email;
    private boolean status = true;
    private LocalDate createAt = LocalDate.now();
}
