package com.example.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText usernameField = findViewById(R.id.username);
        EditText passwordField = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(view -> {
            String username = usernameField.getText().toString();
            String password = passwordField.getText().toString();
            String encodedAuth = Base64.encodeToString(
                    (username + ":" + password).getBytes(), Base64.NO_WRAP);
            Intent intent = new Intent(LoginActivity.this, TaskManagerActivity.class);
            intent.putExtra("authHeader", encodedAuth);
            startActivity(intent);
        });
    }
}
