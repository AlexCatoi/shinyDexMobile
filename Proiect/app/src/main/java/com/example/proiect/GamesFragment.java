package com.example.proiect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;


public class GamesFragment extends Fragment {

    private Button quiz;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_games, container, false);

        quiz=view.findViewById(R.id.btn_quiz);
        // Set Add Pokémon button click listener
        quiz.setOnClickListener(v -> startQuiz());
        return view;
    }

    private void startQuiz() {
        // Create an Intent to navigate from MainActivity to SecondActivity
        Intent QuizActivity = new Intent(getActivity(), QuizActivity.class);

        // Start the new activity
        startActivity(QuizActivity);
    }
}