package com.nassimlnd.flixhub.Controller.Auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.nassimlnd.flixhub.Controller.Auth.Register.RegisterActivity;
import com.nassimlnd.flixhub.Controller.Network.APIClient;
import com.nassimlnd.flixhub.Controller.Profile.ProfileChooserActivity;
import com.nassimlnd.flixhub.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    Toolbar toolbar;

    EditText loginEmail;
    EditText loginPassword;

    TextView loginRegister;

    Button loginButton;

    public static void login(String email, String password, Context ctx) {
        String param = "/auth/login";
        HashMap<String, String> data = new HashMap<>();
        data.put("email", email);
        data.put("password", password);

        ExecutorService executor =
                Executors.newSingleThreadExecutor();
        Handler handler = new
                Handler(Looper.getMainLooper());
        executor.execute(() -> {

            try {
                String result = APIClient.postMethod(param, data, ctx);
                handler.post(() -> {
                    handleResult(result, ctx);
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });
    }

    public static void handleResult(String result, Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();

        try {
            JSONObject jsonObject = new JSONObject(result);

            if (jsonObject.has("errors")) {
                Toast.makeText(ctx, "Invalid email or password", Toast.LENGTH_LONG).show();
                return;
            }

            JSONObject userObject = jsonObject.getJSONObject("user");
            Log.d("TAG", "handleResult: " + userObject);

            editor.putString("email", userObject.getString("email"));
            editor.putString("fullName", userObject.getString("fullName"));
            editor.putString("createdAt", userObject.getString("createdAt"));
            editor.putString("updatedAt", userObject.getString("updatedAt"));
            editor.putBoolean("isLoggedIn", true);
            editor.apply();

            ctx.startActivity(new Intent(ctx, ProfileChooserActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = findViewById(R.id.toolbar);
        loginRegister = findViewById(R.id.loginRegister);
        loginButton = findViewById(R.id.loginButton);
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);

        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        loginRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));

        loginButton.setOnClickListener(v -> {
            String email = loginEmail.getText().toString();
            String password = loginPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                return;
            }

            login(email, password, getApplicationContext());
        });
    }
}
