package com.nassimlnd.flixhub.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.nassimlnd.flixhub.Controller.Network.APIClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class User {

    // Constants
    private static final String LOGIN_ROUTE = "/auth/login";
    private static final String REGISTER_ROUTE = "/auth/register";

    // Attributes
    private String fullName;
    private String email;
    private String createdAt;
    private String updatedAt;

    // Constructors

    public User(String fullName, String email, String createdAt, String updatedAt) {
        this.fullName = fullName;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public User() {
    }

    // Backend methods

    public static boolean login(String email, String password, Context ctx) throws Exception {
        HashMap<String, String> data = new HashMap<>();
        data.put("email", email);
        data.put("password", password);

        //ExecutorService executor = Executors.newSingleThreadExecutor();
        String result = APIClient.postMethod(LOGIN_ROUTE, data, ctx);

        SharedPreferences sharedPreferences = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject userObject = jsonObject.getJSONObject("user");
            Log.d("TAG", "handleResult: " + userObject);

            editor.putString("email", userObject.getString("email"));
            editor.putString("fullName", userObject.getString("fullName"));
            editor.putString("createdAt", userObject.getString("createdAt"));
            editor.putString("updatedAt", userObject.getString("updatedAt"));
            editor.putBoolean("isLoggedIn", true);
            editor.apply();

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public static User register(HashMap<String, String> data, Context ctx) {
        try {
            User user = new User();

            ExecutorService executor = Executors.newSingleThreadExecutor();
            CountDownLatch latch = new CountDownLatch(1);

            executor.execute(() -> {
                try {
                    String result = APIClient.postMethod(REGISTER_ROUTE, data, ctx);
                    SharedPreferences sharedPreferences = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();

                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject userObject = jsonObject.getJSONObject("user");
                    Log.d("TAG", "handleResult: " + userObject);

                    user.setFullName(userObject.getString("fullName"));
                    user.setEmail(userObject.getString("email"));
                    user.setCreatedAt(userObject.getString("createdAt"));
                    user.setUpdatedAt(userObject.getString("updatedAt"));

                    editor.putString("email", userObject.getString("email"));
                    editor.putString("fullName", userObject.getString("fullName"));
                    editor.putString("createdAt", userObject.getString("createdAt"));
                    editor.putString("updatedAt", userObject.getString("updatedAt"));
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();

                    Log.d("TAG", "handleResult: " + userObject.getString("email"));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                latch.countDown();
            });

            latch.await();
            return user;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendRegistrationToken(String token, Context ctx) {
        HashMap<String, String> data = new HashMap<>();
        data.put("fcmToken", token);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        CountDownLatch latch = new CountDownLatch(1);

        executor.execute(() -> {
            try {
                APIClient.postMethodWithCookies("/auth/fcm", data, ctx);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static User getCurrentUser(Context ctx) {
        User user = new User();

        // Get the user from the shared preferences
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        user.setFullName(sharedPreferences.getString("fullName", ""));
        user.setEmail(sharedPreferences.getString("email", ""));
        user.setCreatedAt(sharedPreferences.getString("createdAt", ""));
        user.setUpdatedAt(sharedPreferences.getString("updatedAt", ""));

        return user;
    }

    public boolean update(Context ctx) {
        HashMap<String, String> data = new HashMap<>();
        data.put("fullName", this.fullName);
        data.put("email", this.email);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        CountDownLatch latch = new CountDownLatch(1);

        executor.execute(() -> {
            try {
                APIClient.postMethodWithCookies("/user/update", data, ctx);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return true;
    }

    // Getters and Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
