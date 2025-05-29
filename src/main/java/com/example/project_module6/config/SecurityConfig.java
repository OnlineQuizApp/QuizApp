package com.example.project_module6.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ Cấu hình CORS cho toàn bộ hệ thống
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000")); // Hoặc "*" nếu không bảo mật
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type","Content-Disposition"));
        config.setAllowCredentials(true); // Nếu frontend dùng cookie hoặc token

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ✅ Thêm dòng này
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/account/login", "/api/account/register","/api/user/forgot-password",
                                "/api/user/reset-password","/api/questions/**","/api/category/**","/api/exams/**","/api/user/exams","/api/user/*/questions","/api/exams/submit","/api/exam-set/list","/api/exams/exam-set","/api/results/**","/api/admin/users","/api/exams/statistics").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/questions/upload-file-img","/api/questions/upload-file-img/**").permitAll()
//                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/exams/submit/authenticated","/api/results/user/**").hasRole("USER")


                        .requestMatchers("/api/exam-set/create/confirm/**").hasRole("ADMIN") // Thêm dòng này
                        .requestMatchers(HttpMethod.POST, "/api/exam-set/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/exam-set/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/exam-set/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/exam-set/**").hasAuthority("ROLE_ADMIN")

                        .requestMatchers("/api/user/me","/api/user/update","/api/user/change-password").hasAnyRole("ADMIN","USER")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }



}