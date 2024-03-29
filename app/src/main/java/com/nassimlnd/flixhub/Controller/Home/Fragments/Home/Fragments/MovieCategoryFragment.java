package com.nassimlnd.flixhub.Controller.Home.Fragments.Home.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.nassimlnd.flixhub.Controller.Media.MovieCategoryListActivity;
import com.nassimlnd.flixhub.Model.Media;
import com.nassimlnd.flixhub.Model.Movie;
import com.nassimlnd.flixhub.R;

import java.util.ArrayList;

public class MovieCategoryFragment extends Fragment {

    // View elements
    TextView seeAll;
    TextView categoryTitle;

    // Data
    private final String title;
    private final ArrayList<Movie> data;


    public MovieCategoryFragment(String title, ArrayList<Movie> data) {
        super(R.layout.fragment_home_category);
        this.title = title;
        this.data = data;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_category, container, false);

        categoryTitle = view.findViewById(R.id.categoryTitle);
        seeAll = view.findViewById(R.id.seeAll);

        categoryTitle.setText(title);

        seeAll.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MovieCategoryListActivity.class);
            intent.putExtra("category", title);

            startActivity(intent);
        });

        FragmentManager fragmentManager = getChildFragmentManager();

        for (Movie movie : data) {
            MediaFragment movieFragment = new MediaFragment(movie);
            fragmentManager.beginTransaction().add(R.id.categoryMediaContainer, movieFragment).commit();
        }

        return view;
    }
}
