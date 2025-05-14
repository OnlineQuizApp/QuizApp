package com.example.project_module6.config;

import com.example.project_module6.model.Users;
import com.example.project_module6.repository.IUserRepository;
import com.example.project_module6.service.CustomUserDetailService;
import com.example.project_module6.service.IUserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private CustomUserDetailService userDetailsService;

    String secret = "my_super_secret_key_that_is_long_enough_32_bytes";
    SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

    public String generateToken(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        System.out.println(userDetails);
       Optional<Users> user = userRepository.findByUsername(username);
        System.out.println(user.get().getName());
        List<String> roles = userDetails.getAuthorities().stream()
                .map(auth -> "ROLE_" + auth.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toList());
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .claim("name", user.get().getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 giờ
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}