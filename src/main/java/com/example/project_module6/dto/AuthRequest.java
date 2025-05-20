package com.example.project_module6.dto;

import lombok.Data;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.example.project_module6.config.ValidatorUtil.validateUsername;

@Data
public class AuthRequest implements Validator {
    private String username;
    private String password;

    @Override
    public boolean supports(Class<?> clazz) {
        return AuthRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AuthRequest authRequest = (AuthRequest) target;
        if (username == null || username.trim().isEmpty()) {
            errors.rejectValue("username", null, "Tên đăng nhập không được bỏ trống!");
        } else if (!username.matches("^[a-zA-Z][a-zA-Z0-9]{0,19}$")) {
            errors.rejectValue("username", null, "Tên đăng nhập phải bắt đầu bằng chữ, chỉ gồm chữ hoặc số và tối đa 20 ký tự!");
        }
        if (password == null || password.trim().isEmpty()) {
            errors.rejectValue("password", null, "Mật khẩu không được bỏ trống!");
        } else if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[\\W_]).{8,}$")) {
            errors.rejectValue("password", null, "Mật khẩu phải có ít nhất 8 ký tự, gồm chữ hoa, chữ thường, số và ký tự đặc biệt!");
        }
    }
}