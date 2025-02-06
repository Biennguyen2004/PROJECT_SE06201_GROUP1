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

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class LoginApiController {

    @Autowired
    UserServices userServices;

    @PostMapping("/user/login")
    public ResponseEntity<?> authenticateUser(@RequestBody Login login) {
        // Kiểm tra email tồn tại
        List<String> userEmail = userServices.checkUserEmail(login.getEmail());
        if (userEmail.isEmpty()) {
            return new ResponseEntity<>("Email does not exist", HttpStatus.NOT_FOUND);
        }

        // Kiểm tra mật khẩu
        String hashedPassword = userServices.checkUserPasswordByEmail(login.getEmail());
        if (hashedPassword == null || !BCrypt.checkpw(login.getPassword(), hashedPassword)) {
            return new ResponseEntity<>("Incorrect username or password", HttpStatus.BAD_REQUEST);
        }

        // Lấy thông tin người dùng
        User user = userServices.getUserDetailsByEmail(login.getEmail());
        if (user == null) {
            return new ResponseEntity<>("User details not found", HttpStatus.NOT_FOUND);
        }

        // Trả về thông tin người dùng
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}