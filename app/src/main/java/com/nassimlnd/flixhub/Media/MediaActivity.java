package com.nassimlnd.flixhub.Media;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.nassimlnd.flixhub.Model.Media;
import com.nassimlnd.flixhub.Network.APIClient;
import com.nassimlnd.flixhub.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MediaActivity extends AppCompatActivity {

    ProgressBar loadingSpinner;
    ImageView backBtn;
    ScrollView content;
    ImageView mediaImage;
    TextView mediaTitle;
    TextView mediaDescription;
    TextView mediaRating;
    TextView mediaYear;
    TextView mediaGroupTitle;

    private Media media;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        loadingSpinner = findViewById(R.id.loading_spinner);
        content = findViewById(R.id.mediaContainer);
        mediaImage = findViewById(R.id.imageView);
        mediaTitle = findViewById(R.id.mediaTitle);
        mediaDescription = findViewById(R.id.mediaDescription);
        mediaGroupTitle = findViewById(R.id.mediaGroupTitle);
        backBtn = findViewById(R.id.backButton);

        backBtn.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        int mediaId = getIntent().getIntExtra("mediaId", 0);

        if (mediaId != 0) {
            loadingSpinner.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);

            getMedia(mediaId);
        } else {
            finish();
        }
    }

    public void getMedia(int mediaId) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(getMainLooper());

        executorService.execute(() -> {
            String result = APIClient.callGetMethodWithCookies("/movies/" + mediaId, getApplicationContext());
            handler.post(() -> {
                if (result != null) {
                    Log.d("MediaActivity", result);

                    try {
                        JSONObject mediaObject = new JSONObject(result);
                        JSONObject mediaJson = mediaObject.getJSONObject("movie");

                        Log.d("MediaActivity", mediaJson.toString());

                        media = new Media();

                        media.setId(mediaJson.getInt("id"));
                        media.setTitle(mediaJson.getString("title"));
                        media.setTvg_logo(mediaJson.getString("tvgLogo"));
                        media.setTvg_name(mediaJson.getString("tvgName"));
                        media.setGroup_title(mediaJson.getString("groupTitle"));
                        media.setUrl(mediaJson.getString("url"));

                        mediaTitle.setText(media.getTvg_name().split(String.valueOf("\\("))[0].trim());
                        mediaGroupTitle.setText(media.getGroup_title());

                        getMediaDetails(media);

                        Glide.with(mediaImage.getContext())
                                .load(media.getTvg_logo())
                                .into(mediaImage);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        });
    }

    public void getMediaDetails(Media media) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(getMainLooper());
        String locale = Locale.getDefault().getLanguage();
        StringBuilder lang = new StringBuilder();
        String title = media.getTitle();
        title = title.split(String.valueOf("\\("))[0].trim();

        try {
            switch (locale) {
                case "fr":
                    lang.append("fr-FR");
                    break;
                case "en":
                    lang.append("en-US");
                    break;
                default:
                    lang.append("fr-FR");
                    break;
            }

            String url = "https://api.themoviedb.org/3/search/movie?api_key=bee04557bade921aab4537b991dfb6df&query="+ URLEncoder.encode(title, "UTF-8") +"&language=" + lang;

            executorService.execute(() -> {
                String result = APIClient.getMethodExternalAPI(url);
                handler.post(() -> {
                    Log.d("MediaActivity", result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        JSONObject movie = jsonObject.getJSONArray("results").getJSONObject(0);

                        mediaDescription.setText(movie.getString("overview"));
                        //mediaYear.setText(movie.getString("release_date"));

                        getMediaActors(movie.getString("id"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class Actor {
        private String name;
        private String character;
        private String profilePath;

        public Actor(String name, String character, String profilePath) {
            this.name = name;
            this.character = character;
            this.profilePath = profilePath;
        }

        public String getName() {
            return name;
        }

        public String getCharacter() {
            return character;
        }

        public String getProfilePath() {
            return profilePath;
        }
    }

    public void getMediaActors(String movieId) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(getMainLooper());

        executorService.execute(() -> {

            String url = "http://api.themoviedb.org/3/movie/"+ movieId +"/casts?api_key=bee04557bade921aab4537b991dfb6df";
            String result = APIClient.getMethodExternalAPI(url);

            handler.post(() -> {
                Log.d("MediaActivity", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray actors = jsonObject.getJSONArray("cast");

                    for (int i = 0; i < actors.length(); i++) {
                        JSONObject actor = actors.getJSONObject(i);
                        String name = actor.getString("name");
                        String character = actor.getString("character");
                        String profilePath = actor.getString("profile_path");

                        Actor actor1 = new Actor(name, character, profilePath);
                        Log.d("MediaActivity", actor1.getName());


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }
}
