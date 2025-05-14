package com.example.project_module6.service;

import com.example.project_module6.model.Users;

public interface IUserService {
    boolean existsByUsername(String username);
    void save(Users user);
    boolean existsByEmail(String email);
    public void sendPasswordResetEmail(String toEmail, String resetLink);
}
