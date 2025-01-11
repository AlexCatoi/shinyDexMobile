package com.example.proiect;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HuntManager {

    private Context context;
    private GridLayout container;
    private Activity activity;
    private int counter;
    Button plus;
    Button save;
    // Constructor to get reference to HuntActivity
    public HuntManager(Context context, GridLayout container, Activity activity) {
        this.context=context;
        this.container=container;
        this.activity=activity;
    }
    public void addPokemonHuntToGrid(Pokemon pokemon,String method,String game,boolean charm,boolean luck,int counter) {
        // Inflate the item layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View pokemonItemView = inflater.inflate(R.layout.pokemon_hunt_item, null);

        // Set layout parameters for the inflated view
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.width = GridLayout.LayoutParams.MATCH_PARENT;  // Make it as wide as the GridLayout
        layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;  // Set the height to wrap content
        pokemonItemView.setLayoutParams(layoutParams);

        // Set data for the view
        TextView nameTextView = pokemonItemView.findViewById(R.id.pokemon_info);
        nameTextView.setText(pokemon.getName());

        ImageView pokemonImage = pokemonItemView.findViewById(R.id.pokemon_image);
        Glide.with(context)
                .load(pokemon.getSprites().getFrontShiny())
                .into(pokemonImage);
        TextView encounter=pokemonItemView.findViewById(R.id.pokemon_number);
        encounter.setText(String.valueOf(counter));
        // Add the view to the GridLayout
        container.addView(pokemonItemView);
        SaveToJson(pokemon.getName(),game,method,counter,charm,luck);
        setListner(pokemonItemView);
    }

    public void fetchPokemonHunt(PokemonAPI api, String searchText,String method,String game,boolean charm,boolean luck,int counter) {
        Log.d("update",searchText);
        if (searchText == null || searchText.trim().isEmpty()) {
            Toast.makeText(context, "Please enter a Pokémon name", Toast.LENGTH_SHORT).show();
            return;
        }

        api.getPokemon(searchText.trim().toLowerCase()).enqueue(new Callback<Pokemon>() {
            @Override
            public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Pokemon detailedPokemon = response.body();
                    activity.runOnUiThread(() -> {
                        addPokemonHuntToGrid(detailedPokemon,method,game,charm,luck,counter);
                        });
                } else {
                    activity.runOnUiThread(() -> {
                        Toast.makeText(context, "Pokémon not found", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(Call<Pokemon> call, Throwable t) {
                Log.e("API_ERROR", "Failed to fetch Pokémon by name");
                activity.runOnUiThread(() -> {
                    Toast.makeText(context, "Failed to fetch Pokémon", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void SaveToJson(String name, String game, String method, int counter, boolean charm, boolean luck) {
        JSONObject pokemonData = new JSONObject();
        try {
            pokemonData.put("name", name);
            pokemonData.put("game", game);
            pokemonData.put("method", method);
            pokemonData.put("hadShinyCharm", charm);
            pokemonData.put("hadShinyLuck", luck);
            pokemonData.put("counter", counter);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        // Check for duplicates and save the data
        savePokemonToJsonFile(pokemonData);

        // Display success message
        //Toast.makeText(context, "Hunt Saved!", Toast.LENGTH_SHORT).show();
    }

    private void savePokemonToJsonFile(JSONObject pokemonData) {
        try {
            // File name
            String fileName = "hunts.json";

            // Get the existing JSON array (or create a new one if it doesn't exist)
            JSONArray existingData = readJsonFromFile(fileName);
            if (existingData == null) {
                existingData = new JSONArray();
            }

            // Check for duplicate Pokémon by name
            boolean found = false;
            for (int i = 0; i < existingData.length(); i++) {
                JSONObject existingPokemon = existingData.getJSONObject(i);

                // If the Pokémon already exists, update its data
                if (existingPokemon.getString("name").equalsIgnoreCase(pokemonData.getString("name"))) {
                    existingPokemon.put("game", pokemonData.getString("game"));
                    existingPokemon.put("method", pokemonData.getString("method"));
                    existingPokemon.put("hadShinyCharm", pokemonData.getBoolean("hadShinyCharm"));
                    existingPokemon.put("hadShinyLuck", pokemonData.getBoolean("hadShinyLuck"));
                    existingPokemon.put("counter", pokemonData.getInt("counter"));
                    found = true;
                    break;
                }
            }

            // If not found, add the new Pokémon data
            if (!found) {
                existingData.put(pokemonData);
            }

            // Write the updated array back to the file
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(existingData.toString().getBytes());
            fos.close();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to save data", Toast.LENGTH_SHORT).show();
        }
    }

    public JSONArray readJsonFromFile(String fileName) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
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

private void setListner(View pokeView){
    TextView aux = pokeView.findViewById(R.id.pokemon_number);
    TextView nume = pokeView.findViewById(R.id.pokemon_info);
        plus=pokeView.findViewById(R.id.increment);
        plus.setOnClickListener(v -> {
            int nr=Integer.parseInt(aux.getText().toString());
            nr++;
            aux.setText(String.valueOf(nr));
            pokeView.post(() -> {
                // Ensure that the parent ScrollView can still scroll after updates
                ScrollView scrollView = pokeView.findViewById(R.id.scroll_view_hunt);
                if (scrollView != null) {
                    scrollView.requestLayout();  // Request layout update
                }
            });
        });
        save=pokeView.findViewById(R.id.save);
        save.setOnClickListener(v->{
            int nr=Integer.parseInt(aux.getText().toString());
            JSONArray data=readJsonFromFile("hunts.json");

            for(int i=0;i<data.length();i++)
            {
                try {
                    JSONObject huntData = data.getJSONObject(i);

                    // Extract data from the JSON object
                    String name = huntData.getString("name");

                    if(name.equals(nume.getText())) {
                        String game = huntData.getString("game");
                        String method = huntData.getString("method");
                        boolean charm = huntData.getBoolean("hadShinyCharm");
                        boolean luck = huntData.getBoolean("hadShinyLuck");
                        counter = nr;
                        SaveToJson(name,game,method,counter,charm,luck);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("update","nu ar trebui sa merg");

                }
            }
        });
}

}
