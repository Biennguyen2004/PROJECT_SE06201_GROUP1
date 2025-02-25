package com.example.smarthome.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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
import com.example.smarthome.data.model.request.LoginRequest;
import com.example.smarthome.data.model.response.LoginResponse;
import com.example.smarthome.ui.main.MainActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextView tvGoToSignup;
    private EditText edtEmail, edtPassword;
    private AppCompatButton btnLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initUI();

        goToSignup();

        handleClickLogin();

    }

    private void handleClickLogin() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

    }

    private void validateData() {

        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty()) {
            edtEmail.setError("Enter Email");
            edtEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Enter Email");
            edtEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            edtPassword.setError("Enter Password");
            edtPassword.requestFocus();
            return;
        }

        if (password.length()<8) {
            edtPassword.setError("Password too Short");
            edtPassword.requestFocus();
        }
        else {
            // Ngược lại đầu vào hợp lệ xử lý login
            doLogin(email, password);
        }
    }

    private void doLogin(String email, String password) {

        btnLogin.setEnabled(false);

        ApiService apiService = ApiClient.getInstance().getApiService();

        LoginRequest loginRequest = new LoginRequest(email, password);

        Call<LoginResponse> call = apiService.login(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                btnLogin.setEnabled(true);

                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse != null && "200".equals(loginResponse.getError())) {
                        // Xử lý đăng nhập thành công
                        Toast.makeText(LoginActivity.this, "login thành công", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);

                    } else {
                        // Xử lý lỗi khi đăng nhập không thành công
                        Toast.makeText(LoginActivity.this, loginResponse.getError(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Xử lý khi response không thành công
                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Xử lý lỗi mạng hoặc vấn đề khác
                t.printStackTrace();
                Toast.makeText(LoginActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                btnLogin.setEnabled(true);
            }
        });
    }

    private void initUI() {
        tvGoToSignup = findViewById(R.id.textView23);
        edtEmail = findViewById(R.id.edt_email_login);
        edtPassword = findViewById(R.id.edt_password_login);
        btnLogin = findViewById(R.id.btn_login);
    }

    private void goToSignup() {
        tvGoToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

    }


}