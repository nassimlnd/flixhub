package com.nassimlnd.flixhub.Model;

import android.content.Context;
import android.util.Log;

import com.nassimlnd.flixhub.Controller.Network.APIClient;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Profile {

    // Constants
    public static final String TAG = "Profile";
    public static final String PROFILE_ROUTE = "/profile";

    // Attributes
    private int id;
    private String name;
    private String avatar;
    private String interests;

    public Profile(int id, String name, String avatar, String interests) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.interests = interests;
    }

    public Profile() {

    }

    // Back-end methods

    public static ArrayList<Profile> getProfiles(Context ctx) {
        try {
            ArrayList<Profile> profiles = new ArrayList<>();

            ExecutorService executor = Executors.newSingleThreadExecutor();
            CountDownLatch latch = new CountDownLatch(1);

            executor.execute(() -> {
                // Fetch profiles from the database
                String result = APIClient.callGetMethodWithCookies(PROFILE_ROUTE, ctx);

                Log.d(TAG, "getProfiles: " + result);
                // Parse the result
                try {
                    JSONArray profilesArray = new JSONArray(result);
                    for (int i = 0; i < profilesArray.length(); i++) {
                        profiles.add(new Profile(
                                profilesArray.getJSONObject(i).getInt("id"),
                                profilesArray.getJSONObject(i).getString("name"),
                                profilesArray.getJSONObject(i).getString("avatar"),
                                profilesArray.getJSONObject(i).getString("interests")
                        ));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                latch.countDown();
            });

            latch.await();
            return profiles;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void createProfile() {

    }


    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }
}
