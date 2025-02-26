package com.user_manager_v1.rest_controllers;

import com.user_manager_v1.models.Login;
import com.user_manager_v1.models.User;
import com.user_manager_v1.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1")
public class LoginApiController {

    @Autowired
    UserServices userServices;

    @PostMapping("/user/login")
    public ResponseEntity<Map<String, Object>> authenticateUser(@RequestBody Login login) {
        Map<String, Object> response = new HashMap<>();

        // Kiểm tra email tồn tại
        List<String> userEmail = userServices.checkUserEmail(login.getEmail());
        if (userEmail.isEmpty()) {
            response.put("error", "404");
            response.put("message", "Email does not exist");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // Kiểm tra mật khẩu
        String hashedPassword = userServices.checkUserPasswordByEmail(login.getEmail());
        if (hashedPassword == null || !BCrypt.checkpw(login.getPassword(), hashedPassword)) {
            response.put("error", "401");
            response.put("message", "Incorrect username or password");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // Lấy thông tin người dùng
        User user = userServices.getUserDetailsByEmail(login.getEmail());
        if (user == null) {
            response.put("error", "404");
            response.put("message", "User details not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // Trả về thông tin người dùng khi đăng nhập thành công
        response.put("error", "200");
        response.put("success", "Login successful");
        response.put("user_id", user.getUser_id());
        response.put("username", user.getUsername());
        response.put("phone", user.getPhone());
        response.put("email", user.getEmail());
        response.put("created_at", user.getCreated_at());
        response.put("updated_at", user.getUpdated_at());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

