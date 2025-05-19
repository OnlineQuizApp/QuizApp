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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

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
    public AuthResponse login(@RequestBody AuthRequest request) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                request.getUsername(), request.getPassword());
        authManager.authenticate(authentication);
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtUtil.generateToken(userDetails.getUsername());
        return new AuthResponse(token);
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@Validated @RequestBody UserDto userDto, BindingResult bindingResult){
        if(userService.existsByUsername(userDto.getUsername())){
            return new ResponseEntity<>("Tên đăng nhập đã tồn tại!", HttpStatus.CONFLICT);//409
        }
        if(userService.existsByEmail(userDto.getEmail())){
            return new ResponseEntity<>("Email đăng nhập đã tồn tại!", HttpStatus.CONFLICT);//409
        }
//        if(!userDto.getPassword().equals(userDto.getPasswordConfirm())){
//            return new ResponseEntity<>("Mật khẩu không khớp", HttpStatus.BAD_REQUEST);//400
//        }
        new UserDto().validate(userDto,bindingResult);
        if(bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errors); // 400
        }

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
                return new ResponseEntity<>(new AuthResponse(token),HttpStatus.CREATED); //201
    }
}