package com.user_manager_v1.rest_controllers;


import com.user_manager_v1.dto.UserRegistrationDTO;
import com.user_manager_v1.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class RegisterApiController {

    @Autowired
    UserServices userServices;
    @PostMapping("/user/register")
    public ResponseEntity<Map<String, Object>> registerNewUser(@RequestBody UserRegistrationDTO userDTO) {
        Map<String, Object> response = new HashMap<>();

        // Kiểm tra xem tất cả các trường đã được điền đầy đủ chưa
        if (userDTO.getUsername().isEmpty() ||
                userDTO.getPhone().isEmpty() ||
                userDTO.getEmail().isEmpty() ||
                userDTO.getPassword().isEmpty() ||
                userDTO.getRepassword().isEmpty()) {

            response.put("error", "400");
            response.put("message", "Please complete all fields");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        // Kiểm tra mật khẩu và mật khẩu xác nhận có khớp không
        if (!userDTO.getPassword().equals(userDTO.getRepassword())) {
            response.put("error", "400");
            response.put("message", "Password and Re-password do not match");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Kiểm tra email đã tồn tại trong hệ thống chưa
        List<String> existingEmail = userServices.checkUserEmail(userDTO.getEmail());
        if (!existingEmail.isEmpty()) {
            response.put("error", "400");
            response.put("message", "Email already registered");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Mã hóa mật khẩu
        String hashedPassword = BCrypt.hashpw(userDTO.getPassword(), BCrypt.gensalt());

        // Gọi service để đăng ký người dùng mới
        int result = userServices.registerNewUserServiceMethod(userDTO.getUsername(),
                userDTO.getPhone(),
                userDTO.getEmail(),
                hashedPassword);
        if (result != 1) {
            response.put("error", "400");
            response.put("message", "Registration failed");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        response.put("error", "200");
        response.put("success", "Registration successful");
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
