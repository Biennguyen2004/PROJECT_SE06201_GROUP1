package com.user_manager_v1.services;

import com.user_manager_v1.models.User;
import com.user_manager_v1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServices {
    @Autowired
    UserRepository userRepository;

    // Cập nhật phương thức đăng ký người dùng mới với các trường username, phone, email, password
    public int registerNewUserServiceMethod(String username, String phone, String email, String password) {
        return userRepository.registerNewUser(username, phone, email, password);
    }

    // End of Register New User Service Method.

    // Kiểm tra email đã tồn tại trong hệ thống
    public List<String> checkUserEmail(String email) {
        return userRepository.checkUserEmail(email);
    }

    // Kiểm tra mật khẩu người dùng bằng email
    public String checkUserPasswordByEmail(String email) {
        String password = userRepository.checkUserPasswordByEmail(email);
        if (password == null) {
            throw new RuntimeException("Password not found for email: " + email);
        }
        return password;
    }

    // Lấy thông tin người dùng bằng email
    public User getUserDetailsByEmail(String email) {
        User user = userRepository.GetUserDetailsByEmail(email);
        if (user == null) {
            throw new RuntimeException("User details not found for email: " + email);
        }
        return user;
    }
}
