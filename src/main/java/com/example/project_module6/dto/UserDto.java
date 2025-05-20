package com.example.project_module6.dto;

import com.example.project_module6.model.Users;
import jakarta.validation.ConstraintValidator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto implements Validator {
    private int id;
    private String username;
    private String password;
    private String passwordConfirm;
    private String role = "USER";
    private String name;
    private String email;
    private boolean status = true;
    private LocalDate createAt = LocalDate.now();

    @Override
    public boolean supports(Class<?> clazz) {
        return UserDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserDto userDto = (UserDto) target;
        if (userDto.getUsername() == null || userDto.getUsername().trim().isEmpty()) {
            errors.rejectValue("username", null, "Không được bỏ trống");
        } else {
            if (!userDto.getUsername().matches("^[a-zA-Z0-9]{5,20}$")) {
                errors.rejectValue("username", null, "Tên đăng nhập phải từ 5-20 ký tự, chỉ gồm chữ và số");
            }
        }
        if (userDto.getPassword() == null || userDto.getPassword().trim().isEmpty()) {
            errors.rejectValue("password", null, "Không được bỏ trống");
        } else if (!userDto.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[\\W_]).{8,}$")) {
            errors.rejectValue("password", null, "Mật khẩu phải có ít nhất 8 ký tự, gồm chữ hoa, chữ thường, số và ký tự đặc biệt!");
        } else if (!userDto.getPassword().equals(userDto.getPasswordConfirm())) {
            errors.rejectValue("passwordConfirm", null, "Mật Khẩu phải không khớp");
        }

        if (userDto.getEmail() == null || userDto.getEmail().trim().isEmpty()) {
            errors.rejectValue("email", null, "Email không được để trống");
        } else if (!userDto.getEmail().matches("^[A-Za-z0-9_]+@[A-Za-z0-9.]+$")) {
            errors.rejectValue("email", null, "Email không đúng định dạng");
        }

        if (userDto.getName() == null || userDto.getName().trim().isEmpty()) {
            errors.rejectValue("name", null, "Họ và tên không được để trống");
        } else if (!userDto.getName().matches("^[\\p{L} ]{5,50}$")) {
            errors.rejectValue("name", null, "Họ và tên chỉ chứa chữ cái và khoảng trắng (5–50 ký tự)");
        } else {
            String[] words = userDto.getName().trim().split("\\s+");
            for (String word : words) {
                if (word.length() > 0 && !Character.isUpperCase(word.charAt(0))) {
                    errors.rejectValue("name", null, "Mỗi chữ cái đầu của họ tên phải viết hoa");
                    break;
                }
            }
        }
    }
}
