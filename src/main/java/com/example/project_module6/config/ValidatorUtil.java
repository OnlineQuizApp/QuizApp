package com.example.project_module6.config;

import org.springframework.validation.Errors;

import java.time.LocalDateTime;

public class ValidatorUtil {
    private static final String usernameRegex = "^[a-zA-Z][a-zA-Z0-9]{0,19}$";
    private static final String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[\\W_]).{8,}$";
    private static final String nameRegex = "^(?:[A-ZÀ-Ỹ][a-zà-ỹ]*\\s)*[A-ZÀ-Ỹ][a-zà-ỹ]{0,28}$";
    private static final String emailRegex = "^[A-Za-z][A-Za-z0-9_]*@[^.][A-Za-z0-9_]*(?:\\.[A-Za-z0-9_]+){1}$";

    public static void validateUsername(String username, Errors errors) {
        if (username == null || username.trim().isEmpty()) {
            errors.rejectValue("username", null, "Tên đăng nhập không được bỏ trống!");
        } else if (!username.matches(usernameRegex)) {
            errors.rejectValue("username", null, "Tên đăng nhập phải bắt đầu bằng chữ, chỉ gồm chữ hoặc số và tối đa 20 ký tự!");
        }
    }

    public static void validatePassword(String password, Errors errors) {
        if (password == null || password.trim().isEmpty()) {
            errors.rejectValue("password", null, "Mật khẩu không được bỏ trống!");
        } else if (!password.matches(passwordRegex)) {
            errors.rejectValue("password", null, "Mật khẩu phải có ít nhất 8 ký tự, gồm chữ hoa, chữ thường, số và ký tự đặc biệt!");
        }
    }

    public static void validatePasswordConfirm(String password, String passwordConfirm, Errors errors) {
        if (password != null && !password.trim().isEmpty()) {
            if (passwordConfirm == null || !passwordConfirm.equals(password)) {
                errors.rejectValue("passwordConfirm", null, "Mật khẩu không khớp!");
            }
        }
    }

    public static void validateRole(String role, Errors errors) {
        if (role == null || role.trim().isEmpty()) {
            errors.rejectValue("role", null, "Vai trò của người dùng chưa có giá trị!");
        }
    }

    public static void validateStatus(boolean status, Errors errors) {
        if (!status) {
            errors.rejectValue("status", null, "Tài khoản đã bị khóa không thể đăng nhập!");
        }
    }

    public static void validateName(String name, Errors errors) {
        if (name == null || name.trim().isEmpty()) {
            errors.rejectValue("name", null, "Họ tên không được để trống!");
        } else if (!name.matches(nameRegex)) {
            errors.rejectValue("name", null, "Họ tên phải viết hoa chữ cái đầu, chỉ gồm chữ cái và không quá 30 ký tự!");
        }
    }

    public static void validateEmail(String email, Errors errors) {
        if (email == null || email.trim().isEmpty()) {
            errors.rejectValue("email", null, "Email không được để trống!");
        } else if (!email.matches(emailRegex)) {
            errors.rejectValue("email", null, "Email không đúng định dạng!");
        }
    }

    public static void validateCreatedAt(LocalDateTime createdAt, Errors errors) {
        if (createdAt == null) {
            errors.rejectValue("createdAt", null, "Ngày tạo không được để trống!");
        } else if (createdAt.isAfter(LocalDateTime.now())) {
            errors.rejectValue("createdAt", null, "Ngày tạo không được lớn hơn thời điểm hiện tại!");
        }
    }

    public static void validateSoftDelete(boolean softDelete, Errors errors) {
        if (softDelete) {
            errors.rejectValue("softDelete", null, "Tài khoản đã bị xóa!");
        }
    }
}
