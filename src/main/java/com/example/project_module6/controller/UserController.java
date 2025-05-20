package com.example.project_module6.controller;

import com.example.project_module6.config.JwtUtil;
import com.example.project_module6.dto.ChangePasswordRequestDto;
import com.example.project_module6.dto.ForgotPasswordRequestDto;
import com.example.project_module6.dto.ResetPasswordRequestDto;
import com.example.project_module6.dto.UserDto;
import com.example.project_module6.model.Users;
import com.example.project_module6.repository.IUserRepository;
import com.example.project_module6.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IUserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserFromContext(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        System.out.println("Logged in as: " + authentication.getName());

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<Users> userOpt = userRepository.findByUsername(userDetails.getUsername());

        if (userOpt.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        Users user = userOpt.get();
        UserDto userDto = new UserDto(
                user.getId(),
                user.getUsername(),
                null,
                null,
                user.getRole().toString(),
                user.getName(),
                user.getEmail(),
                user.isStatus(),
                user.getCreateAt()
        );

        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(Authentication authentication, @RequestBody UserDto dto) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<Users> userOpt = userRepository.findByUsername(userDetails.getUsername());

        if (userOpt.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        Users currentUser = userOpt.get();
        currentUser.setName(dto.getName());
        currentUser.setEmail(dto.getEmail());
        userRepository.save(currentUser);

        return ResponseEntity.ok("Cập nhật thành công");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequestDto request){
        Optional<Users> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Users user = userOpt.get();
        String token = jwtUtil.generateResetToken(user.getUsername());
        String resetLink = "http://localhost:3000/reset-password?token=" + token;
        userService.sendPasswordResetEmail(user.getEmail(), resetLink);
        return ResponseEntity.ok("Reset email sent");

    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDto request) {
        String token = request.getToken();
        String newPassword = request.getNewPassword();
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Token is missing.");
        }
        if (newPassword == null) {
            return ResponseEntity.badRequest().body("Password not null");
        }
        System.out.println("token là : "+token);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token");
        }
        String username = jwtUtil.extractUsername(token);
        Optional<Users> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return ResponseEntity.notFound().build();
        Users user = userOpt.get();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("Password reset successfully");
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> resetPassword(@RequestBody ChangePasswordRequestDto request, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        String token = request.getToken();
        System.out.println("tokenchange"+token);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<Users> userOpt = userRepository.findByUsername(userDetails.getUsername());

        if (userOpt.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        Users currentUser = userOpt.get();
        if (!passwordEncoder.matches(request.getOldPassword(), currentUser.getPassword())) {
            return new ResponseEntity<>("Mật khẩu không đúng", HttpStatus.BAD_REQUEST);
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return new ResponseEntity<>("Mật khẩu không khớp", HttpStatus.CONFLICT);
        }
        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.save(currentUser);
        return ResponseEntity.ok("Password reset successfully");
    }

}
