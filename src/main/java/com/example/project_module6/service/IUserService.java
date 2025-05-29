package com.example.project_module6.service;

import com.example.project_module6.dto.UserFilterRequest;
import com.example.project_module6.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUserService {
    boolean existsByUsername(String username);
    void save(Users user);
    boolean existsByEmail(String email);
    public void sendPasswordResetEmail(String toEmail, String resetLink);
    Page<Users> findUsers(UserFilterRequest filter, Pageable pageable);
    Users toggleUserStatus(int id);
    Users restoreUser(int id);
}
