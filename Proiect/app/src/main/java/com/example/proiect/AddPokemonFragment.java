package com.example.proiect;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddPokemonFragment extends Fragment {

    private EditText inputPokemonName;
    private Spinner spinnerGame;
    private EditText inputMethod;
    private CheckBox checkboxShinyCharm;
    private CheckBox checkboxShinyLuck;
    private Button btnAddPokemon;

    private List<String> gameList; // List of games for spinner

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pokemon_add, container, false);

        // Initialize UI components
        inputPokemonName = view.findViewById(R.id.input_pokemon_name);
        spinnerGame = view.findViewById(R.id.spinner_game);
        inputMethod = view.findViewById(R.id.input_method);
        checkboxShinyCharm = view.findViewById(R.id.checkbox_shiny_charm);
        checkboxShinyLuck = view.findViewById(R.id.checkbox_shiny_luck);
        btnAddPokemon = view.findViewById(R.id.btn_add_pokemon);

        // Set Add Pokémon button click listener
        btnAddPokemon.setOnClickListener(v -> addPokemon());

        return view;
    }

    private void addPokemon() {
        // Get data from UI
        String pokemonName = inputPokemonName.getText().toString().trim().toLowerCase();
        String selectedGame = spinnerGame.getSelectedItem().toString();
        String methodUsed = inputMethod.getText().toString().trim();
        boolean hadShinyCharm = checkboxShinyCharm.isChecked();
        boolean hadShinyLuck = checkboxShinyLuck.isChecked();

        // Validate input
        if (pokemonName.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a Pokémon name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (methodUsed.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a method used", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject pokemonData = new JSONObject();
        try {
            pokemonData.put("name", pokemonName);
            pokemonData.put("game", selectedGame);
            pokemonData.put("method", methodUsed);
            pokemonData.put("hadShinyCharm", hadShinyCharm);
            pokemonData.put("hadShinyLuck", hadShinyLuck);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to create JSON data", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save JSON to a file
        savePokemonToJsonFile(pokemonData);

        // Display success message
        Toast.makeText(getContext(), "Pokémon added successfully!", Toast.LENGTH_SHORT).show();

        // Optionally, clear the fields after adding
        clearFields();
    }

    private void clearFields() {
        inputPokemonName.setText("");
        spinnerGame.setSelection(0);
        inputMethod.setText("");
        checkboxShinyCharm.setChecked(false);
        checkboxShinyLuck.setChecked(false);
    }

    private void savePokemonToJsonFile(JSONObject pokemonData) {
        try {
            // File name
            String fileName = "pokemon_caught_data.json";

            // Get the existing JSON array (or create a new one if it doesn't exist)
            JSONArray existingData = readJsonFromFile(fileName);
            if (existingData == null) {
                existingData = new JSONArray();
            }

            // Add new Pokémon data
            existingData.put(pokemonData);

            // Write the updated array back to the file
            FileOutputStream fos = getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(existingData.toString().getBytes());
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to save data", Toast.LENGTH_SHORT).show();
        }
    }

    private JSONArray readJsonFromFile(String fileName) {
        try {
            FileInputStream fis = getContext().openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            br.close();
            isr.close();
            fis.close();

            // Parse JSON array
            return new JSONArray(sb.toString());
        } catch (FileNotFoundException e) {
            // File not found, return null (we'll create a new file later)
            return null;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}