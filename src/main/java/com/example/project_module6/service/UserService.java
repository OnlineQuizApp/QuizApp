package com.example.project_module6.service;

import com.example.project_module6.dto.UserFilterRequest;
import com.example.project_module6.model.Users;
import com.example.project_module6.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService{
    @Autowired
    private IUserRepository userRepository;
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public void save(Users user) {
        userRepository.save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Password Reset Request");
        message.setText("Click the link below to reset your password:\n" + resetLink);
        message.setFrom("your_email@gmail.com");

        mailSender.send(message);
    }
    @Override
    public Page<Users> findUsers(UserFilterRequest filter, Pageable pageable) {
        return userRepository.findByFilters(
                filter.getName(),
                filter.getRoleEnum(),
                filter.getStatus(),
                filter.getSoftDelete(),
                filter.getCreatedAt(),
                pageable
        );
    }
    @Override
    public Users toggleUserStatus(int id) {
        Users user = userRepository.findById(id);
        if(user==null){
            throw new RuntimeException("Không tìm thấy người dùng");
        }
        user.setStatus(!user.isStatus());
        return userRepository.save(user);
    }
    @Override
    public Users restoreUser(int id) {
        Users user = userRepository.findById(id);
        if(user==null){
            throw new RuntimeException("Không tìm thấy người dùng");
        }
        user.setSoftDelete(false);
        return userRepository.save(user);
    }

}
