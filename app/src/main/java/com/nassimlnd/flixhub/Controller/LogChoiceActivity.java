package com.nassimlnd.flixhub.Controller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.nassimlnd.flixhub.Controller.Auth.LoginActivity;
import com.nassimlnd.flixhub.Controller.Auth.Register.RegisterActivity;
import com.nassimlnd.flixhub.R;

public class LogChoiceActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView loginRegister;
    Button loginActivity;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_choice);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        loginRegister = findViewById(R.id.loginRegister);
        loginRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));

        loginActivity = findViewById(R.id.loginActivity);
        loginActivity.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
    }
}
