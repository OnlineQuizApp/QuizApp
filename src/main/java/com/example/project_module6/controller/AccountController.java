package com.example.project_module6.controller;

import com.example.project_module6.dto.AuthRequest;
import com.example.project_module6.dto.AuthResponse;
import com.example.project_module6.config.JwtUtil;
import com.example.project_module6.dto.UserDto;
import com.example.project_module6.model.Users;
import com.example.project_module6.service.CustomUserDetailService;
import com.example.project_module6.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/account")
@CrossOrigin(origins = "*")
public class AccountController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private CustomUserDetailService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private IUserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody AuthRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<Map<String, String>> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> Map.of("field", error.getField(), "message", error.getDefaultMessage()))
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors); // 400
        }
        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    request.getUsername(), request.getPassword());
            authManager.authenticate(authentication);
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            String token = jwtUtil.generateToken(userDetails.getUsername());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (AuthenticationException e) {
            List<Map<String, String>> errors = List.of(
                    Map.of("field", "general", "message", "Tên đăng nhập hoặc mật khẩu không đúng!")
            );
            return ResponseEntity.badRequest().body(errors); // 400
        } catch (Exception e) {
            List<Map<String, String>> errors = List.of(
                    Map.of("field", "general", "message", "Đã có lỗi xảy ra, vui lòng thử lại!")
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors); // 500
        }
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@Validated @RequestBody UserDto userDto, BindingResult bindingResult) {
        if (userService.existsByUsername(userDto.getUsername())) {
            List<Map<String, String>> errors = List.of(
                    Map.of("field", "username", "message", "Tên đăng nhập đã tồn tại!")
            );
            return new ResponseEntity<>(errors, HttpStatus.CONFLICT); // 409
        }
        if (userService.existsByEmail(userDto.getEmail())) {
            List<Map<String, String>> errors = List.of(
                    Map.of("field", "email", "message", "Email đã tồn tại!")
            );
            return new ResponseEntity<>(errors, HttpStatus.CONFLICT); // 409
        }
        if (bindingResult.hasErrors()) {
            List<Map<String, String>> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> Map.of("field", error.getField(), "message", error.getDefaultMessage()))
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors); // 400
        }

        try {
            Users user = new Users();
            user.setUsername(userDto.getUsername());
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            user.setName(userDto.getName());
            user.setEmail(userDto.getEmail());
            user.setCreateAt(LocalDate.now());
            user.setRole(Users.Role.valueOf(userDto.getRole()));
            user.setStatus(userDto.isStatus());
            userService.save(user);
            String token = jwtUtil.generateToken(user.getUsername());
            return new ResponseEntity<>(new AuthResponse(token), HttpStatus.CREATED); // 201
        } catch (Exception e) {
            List<Map<String, String>> errors = List.of(
                    Map.of("field", "general", "message", "Đã có lỗi xảy ra, vui lòng thử lại!")
            );
            return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
}