package com.example.smarthome.ui.auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smarthome.R;
import com.example.smarthome.data.api.ApiClient;
import com.example.smarthome.data.api.ApiService;
import com.example.smarthome.data.model.request.SignupRequest;
import com.example.smarthome.data.model.response.SignupResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    private TextView tvGoToLogin;
    private EditText inputFullName, inputPhone, inputEmail, inputPassword, inputRePassword;
    private AppCompatButton btnSignup;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initUI();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private void validateData() {

            String fullName = inputFullName.getText().toString().trim();
            String phone = inputPhone.getText().toString().trim();
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            String rePassword = inputRePassword.getText().toString().trim();

            // Kiểm tra tên đầy đủ
            if (fullName.isEmpty()) {
                inputFullName.setError("Enter full name");
                inputFullName.requestFocus();
                return;
            }

            // Kiểm tra số điện thoại
            if (phone.isEmpty()) {
                inputPhone.setError("Enter phone number");
                inputPhone.requestFocus();
                return;
            }
            if (!phone.matches("\\d{10,11}")) {
                inputPhone.setError("Invalid phone number (must be 10-11 digits)");
                inputPhone.requestFocus();
                return;
            }

            // Kiểm tra email
            if (email.isEmpty()) {
                inputEmail.setError("Enter Email");
                inputEmail.requestFocus();
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                inputEmail.setError("Invalid Email");
                inputEmail.requestFocus();
                return;
            }

            // Kiểm tra mật khẩu
            if (password.isEmpty()) {
                inputPassword.setError("Enter password");
                inputPassword.requestFocus();
                return;
            }
            if (password.length() < 8) {
                inputPassword.setError("Password must be at least 6 characters");
                inputPassword.requestFocus();
                return;
            }

            // Kiểm tra re-password
            if (rePassword.isEmpty()) {
                inputRePassword.setError("Enter Re-password");
                inputRePassword.requestFocus();
                return;
            }
            if (!rePassword.equals(password)) {
                inputRePassword.setError("Passwords do not match");
                inputRePassword.requestFocus();
                return;
            }

            // Nếu tất cả hợp lệ, tiếp tục gọi API đăng ký
            doRegister(fullName, phone, email, password, rePassword);
    }

    private void doRegister(String fullname, String phone, String email, String password, String rePassword) {

        btnSignup.setEnabled(false);

        ApiService apiService = ApiClient.getInstance().getApiService();

        SignupRequest signupRequest = new SignupRequest(fullname, phone, email, password, rePassword);

        Call<SignupResponse> call = apiService.register(signupRequest);

        call.enqueue((new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {

                btnSignup.setEnabled(true);

                if (response.isSuccessful()) {
                    SignupResponse resp = response.body();

                    // Kiểm tra thông báo thành công
                    if (resp != null && resp.getError().equals("200")) {
                        Toast.makeText(SignupActivity.this, "Register thành công", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        // Xử lý các lỗi từ server nếu có
                        Toast.makeText(SignupActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Xử lý khi response không thành công, ví dụ mã lỗi HTTP khác
                    Toast.makeText(SignupActivity.this, "Registration failed! Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                // Xử lý lỗi mạng hoặc vấn đề khác
                t.printStackTrace();
                Toast.makeText(SignupActivity.this, "Network error. Please try again later.", Toast.LENGTH_SHORT).show();
                btnSignup.setEnabled(true);
            }
        }));

    }


    private void initUI() {
        tvGoToLogin = findViewById(R.id.textView17);
        inputFullName = findViewById(R.id.edt_fullname);
        inputPhone = findViewById(R.id.edt_phone);
        inputEmail = findViewById(R.id.edt_email);
        inputPassword = findViewById(R.id.edt_password);
        inputRePassword = findViewById(R.id.edt_repassword);
        btnSignup = findViewById(R.id.btn_signup);




        tvGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}