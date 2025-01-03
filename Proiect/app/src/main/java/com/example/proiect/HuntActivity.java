package com.example.proiect;

import android.content.Context;
import android.database.CursorJoiner;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class HuntActivity extends AppCompatActivity {

    private GridLayout container;
    private HuntManager hunt;
    private PokemonAPI api;
    private String name;
    private String game;
    private String method;
    private boolean charm;
    private boolean luck;
    int counter=0;

    private Button start;
    private EditText nameField;
    private CheckBox CharmBox;
    private CheckBox LuckBox;
    private Spinner gameField;
    private Spinner methodField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hunt);  // Reference your layout file here
        //deleteJsonFile(this,"hunts.json");
        // Set up the toolbar and enable the back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GridLayout container=findViewById(R.id.pokemon_container);
        start=findViewById(R.id.btn_start_hunt);
        nameField=findViewById(R.id.input_pokemon_name);
        gameField=findViewById(R.id.spinner_game);
        methodField=findViewById(R.id.spinner_method);
        CharmBox=findViewById(R.id.checkbox_shiny_charm);
        LuckBox=findViewById(R.id.checkbox_shiny_luck);

        api = ApiClient.getClient().create(PokemonAPI.class);
        hunt=new HuntManager(this,container,this);
        start.setOnClickListener(v -> startHunt());
        initialize();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void startHunt(){
        name=nameField.getText().toString();
        game=gameField.getSelectedItem().toString();
        method=methodField.getSelectedItem().toString();
        charm=CharmBox.isChecked();
        luck=LuckBox.isChecked();
        hunt.fetchPokemonHunt(api,name,method,game,charm,luck,0);
    }
    private void initialize() {
        // Read existing data from the JSON file
        JSONArray existingData = hunt.readJsonFromFile("hunts.json");
        // Check if the data is not empty
        if (existingData != null) {
            // Iterate through each JSON object in the array
            for (int i = 0; i < existingData.length(); i++) {
                try {
                    // Get the JSON object at the current index
                    JSONObject huntData = existingData.getJSONObject(i);

                    // Extract data from the JSON object
                    name = huntData.getString("name");
                    game = huntData.getString("game");
                    method = huntData.getString("method");
                    charm =huntData.getBoolean("hadShinyCharm");
                    luck = huntData.getBoolean("hadShinyLuck");
                    counter=huntData.getInt("counter");

                    hunt.fetchPokemonHunt(api,name,method,game,charm,luck,counter);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("update","nu ar trebui sa merg");

                }
            }
        }
    }
    public static boolean deleteJsonFile(Context context, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        if (file.exists()) {
            return file.delete(); // Returns true if the file was successfully deleted
        }
        return false; // File does not exist
    }
}