package com.example.smarthome.data.model.Request;

public class SignupRequest {

    private String username;
    private String phone;
    private String email;
    private String password;
    private String repassword;

    public SignupRequest(String fullname, String phone, String email, String password, String repassword) {
        this.username = fullname;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.repassword = repassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepassword() {
        return repassword;
    }

    public void setRepassword(String repassword) {
        this.repassword = repassword;
    }
}
