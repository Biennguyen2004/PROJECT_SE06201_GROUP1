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

    public int registerNewUserServiceMethod(String fname, String lname, String email,String password){
        return userRepository.registerNewUser(fname,lname,email,password);
    }


    // End Of Register New User Service Method.

    public List<String> checkUserEmail(String email) {
        return userRepository.checkUserEmail(email);
    }

    public String checkUserPasswordByEmail(String email) {
        String password = userRepository.checkUserPasswordByEmail(email);
        if (password == null) {
            throw new RuntimeException("Password not found for email: " + email);
        }
        return password;
    }

    public User getUserDetailsByEmail(String email) {
        User user = userRepository.GetUserDetailsByEmail(email);
        if (user == null) {
            throw new RuntimeException("User details not found for email: " + email);
        }
        return user;
    }


}
