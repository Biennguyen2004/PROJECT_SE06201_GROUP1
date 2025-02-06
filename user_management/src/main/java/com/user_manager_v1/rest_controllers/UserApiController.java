package com.user_manager_v1.rest_controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserApiController {
    @GetMapping("/test")
    public String testEndpoint(){
        return "hihi";
    }
}
