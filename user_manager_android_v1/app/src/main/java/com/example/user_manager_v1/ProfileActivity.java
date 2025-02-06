package com.example.user_manager_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    TextView tv_first_name, tv_last_name, tv_email;
    Button sign_out_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Hook Text View Objects:
        tv_first_name = findViewById(R.id.first_name);
        tv_last_name = findViewById(R.id.last_name);
        tv_email = findViewById(R.id.email);

        // Get Intent Extra Values:
        String first_name = getIntent().getStringExtra("first_name");
        String last_name = getIntent().getStringExtra("last_name");
        String email = getIntent().getStringExtra("email");

        // Set Text View Profile Values:
        tv_first_name.setText(first_name);
        tv_last_name.setText(last_name);
        tv_email.setText(email);

        // Hook Sign Out Button:
        sign_out_btn = findViewById(R.id.sign_out_btn);

        // Set On Click Listener:
        sign_out_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUserOut();
            }
        });
    }

    public void signUserOut(){

        // Set Text View Profile Values:
        tv_first_name.setText(null);
        tv_last_name.setText(null);
        tv_email.setText(null);
        
        // Return User Back To Home:
        Intent goToHome = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(goToHome);
        finish();

    }
}