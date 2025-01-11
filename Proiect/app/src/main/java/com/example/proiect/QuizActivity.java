package com.example.proiect;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class QuizActivity extends AppCompatActivity {
    private TextView question,points,result;
    private Button btn1,btn2,btn3,btn4,play;
    private JSONArray questionsArray;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int totalQuestions = 8;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);  // Reference your layout file here
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //saveQuizQuestions();
        question=findViewById(R.id.tv_question);
        btn1=findViewById(R.id.btn_answer1);
        btn2=findViewById(R.id.btn_answer2);
        btn3=findViewById(R.id.btn_answer3);
        btn4=findViewById(R.id.btn_answer4);
        points=findViewById(R.id.tv_score);
        result=findViewById(R.id.tv_result);
        play=findViewById(R.id.btn_play_again);

        loadQuestions();

        // Display the first question
        displayQuestion();

        // Set listeners for the buttons
        btn1.setOnClickListener(v -> checkAnswer(btn1.getText().toString()));
        btn2.setOnClickListener(v -> checkAnswer(btn2.getText().toString()));
        btn3.setOnClickListener(v -> checkAnswer(btn3.getText().toString()));
        btn4.setOnClickListener(v -> checkAnswer(btn4.getText().toString()));

        play.setOnClickListener(v -> restartQuiz());
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void loadQuestions() {
            try {
                // Read the JSON file from assets (or another location if needed)
                String jsonString = readJsonFromFile("quiz.json");
                if (jsonString != null) {
                    questionsArray = new JSONArray(jsonString);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    private String readJsonFromFile(String fileName) {
        StringBuilder sb = new StringBuilder();
        try (FileInputStream fis = openFileInput(fileName);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (FileNotFoundException e) {
            return null; // No file found, returning null
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    // Display the current question and answers
    private void displayQuestion() {
        if (questionsArray == null || currentQuestionIndex >= totalQuestions) {
            showResults();
            Toast.makeText(this, "Quiz Completed!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject currentQuestion = questionsArray.getJSONObject(currentQuestionIndex);
            String questionText = currentQuestion.getString("question");
            JSONArray answers = currentQuestion.getJSONArray("answers");

            // Set the question text
            question.setText(questionText);

            // Set the answers on the buttons
            btn1.setText(answers.getString(0));
            btn2.setText(answers.getString(1));
            btn3.setText(answers.getString(2));
            btn4.setText(answers.getString(3));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Check the selected answer
    private void checkAnswer(String selectedAnswer) {
        try {
            JSONObject currentQuestion = questionsArray.getJSONObject(currentQuestionIndex);
            String correctAnswer = currentQuestion.getString("correct");

            // Check if the answer is correct
            if (selectedAnswer.equalsIgnoreCase(correctAnswer))
            {
                score++;
            }
            points.setText(score + "/" + questionsArray.length());
            // Move to the next question
            currentQuestionIndex++;
            displayQuestion();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void showResults() {
        question.setVisibility(View.GONE);
        btn1.setVisibility(View.GONE);
        btn2.setVisibility(View.GONE);
        btn3.setVisibility(View.GONE);
        btn4.setVisibility(View.GONE);
        result.setText("You have " + score + "/" + totalQuestions + " correct answers");
        result.setVisibility(View.VISIBLE);
        play.setVisibility(View.VISIBLE);
    }
    private void restartQuiz() {
        score = 0;
        currentQuestionIndex = 0;
        points.setText("0/" + totalQuestions);
        result.setVisibility(View.GONE);
        play.setVisibility(View.GONE);
        question.setVisibility(View.VISIBLE);
        btn1.setVisibility(View.VISIBLE);
        btn2.setVisibility(View.VISIBLE);
        btn3.setVisibility(View.VISIBLE);
        btn4.setVisibility(View.VISIBLE);
        loadQuestions();
        displayQuestion();
    }
/*
    private void writeJsonToFile(String fileName) {
        try {
            // Prepare the JSON data
            JSONArray questionsArray = new JSONArray();

            // Question 1
            JSONObject question1 = new JSONObject();
            question1.put("question", "What is the color of shiny Quagsire?");
            question1.put("answers", new JSONArray().put("Red").put("Pink").put("Blue").put("Green"));
            question1.put("correct", "Pink");
            questionsArray.put(question1);

            // Question 2
            JSONObject question2 = new JSONObject();
            question2.put("question", "How many Unown forms are?");
            question2.put("answers", new JSONArray().put("23").put("19").put("28").put("32"));
            question2.put("correct", "28");
            questionsArray.put(question2);

            // Question 3
            JSONObject question3 = new JSONObject();
            question3.put("question", "How many evolution branches does eevee has");
            question3.put("answers", new JSONArray().put("2").put("3").put("7").put("8"));
            question3.put("correct", "8");
            questionsArray.put(question3);

            // Question 4
            JSONObject question4 = new JSONObject();
            question4.put("question", "What move can't K.O a pokemon?");
            question4.put("answers", new JSONArray().put("False Swipe").put("Icicle Crash").put("Roar of Time").put("Guillotine"));
            question4.put("correct", "False Swipe");
            questionsArray.put(question4);

            // Question 5
            JSONObject question5 = new JSONObject();
            question5.put("question", "Which pokemon is not a normal type?");
            question5.put("answers", new JSONArray().put("Komala").put("Kangaskhan").put("PorygonZ").put("Tadbulb"));
            question5.put("correct", "Tadbulb");
            questionsArray.put(question5);

            // Question 6
            JSONObject question6 = new JSONObject();
            question6.put("question", "Which pokemon is not from gen 2?");
            question6.put("answers", new JSONArray().put("Miltank").put("Sharpedo").put("Azumarill").put("Magcargo"));
            question6.put("correct", "Sharpedo");
            questionsArray.put(question6);

            // Question 7
            JSONObject question7 = new JSONObject();
            question7.put("question", "Which is a valid method of shiny hunt?");
            question7.put("answers", new JSONArray().put("Masuda").put("Breeding").put("All").put("Mass Outbreak"));
            question7.put("correct", "All");
            questionsArray.put(question7);

            // Question 8
            JSONObject question8 = new JSONObject();
            question8.put("question", "Which pokemon is the ace of Cynthia?");
            question8.put("answers", new JSONArray().put("Roserade").put("Spiritomb").put("Milotic").put("Garchomp"));
            question8.put("correct", "Garchomp");
            questionsArray.put(question8);

            // Save the JSON to the file
            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(questionsArray.toString().getBytes());
            fos.close();

            // Inform the user that the file is saved
            Toast.makeText(this, "Questions saved successfully!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save questions!", Toast.LENGTH_SHORT).show();
        }
    }

    // You can call this method to write the JSON data to a file
    private void saveQuizQuestions() {
        writeJsonToFile("quiz.json"); // Use any file name you prefer
    }
    */
}
