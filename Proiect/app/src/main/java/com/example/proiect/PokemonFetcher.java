package com.example.proiect;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PokemonFetcher {
    private GridLayout pokemonContainer;
    private Context context;  // Context for UI operations
    private Activity activity;
    private int currentOffset=0;  // Tracks the current position in the list
    private final int PAGE_SIZE=50;  // Number of Pokémon per page
    private boolean isLoading = false;  // Prevent multiple concurrent loads
    private final int totalPokemons = 1025; // Total Pokémon to fetch
    private boolean isRefreshing = false;
    private Call<PokemonListResponse> currentCall;  // Reference to the ongoing call
    private List<PokemonListResponse.PokemonResult> pokemonList;
    private Thread fetchThread;

    public PokemonFetcher(Context context, Activity activity, GridLayout pokemonContainer) {
        this.context = context;
        this.activity = activity;
        this.pokemonContainer=pokemonContainer;
        this.pokemonList = new ArrayList<>();
        currentOffset=0;
    }


    public void fetchPokemonBatch(PokemonAPI api, String searchText, List<String> seeAll, String type, String game) {
        // Prevent fetching if we're already loading or no more Pokémon are available
        isRefreshing=false;
        if (isLoading || currentOffset >= totalPokemons || isRefreshing) return; // Stop if already loading, already fetched all, or refreshing is in progress

        isLoading = true;

        // Clear filteredResults before starting a new batch to avoid old data
        List<PokemonListResponse.PokemonResult> filteredResults = new ArrayList<>();
        Log.d("Fetching Batch", "Current Offset: " + currentOffset);
        // Fetch the next batch of Pokémon
        currentCall = api.getPokemonList(50, currentOffset);
        currentCall.enqueue(new Callback<PokemonListResponse>() {
            @Override
            public void onResponse(Call<PokemonListResponse> call, Response<PokemonListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PokemonListResponse.PokemonResult> results = response.body().getResults();

                    if (results.isEmpty()) {
                        isLoading = false; // No more Pokémon to fetch
                        return;
                    }

                    // Reset isRefreshing once the new batch of results is successfully processed
                    isRefreshing = false;

                    // Use CountDownLatch to process the batch completely before updating the UI
                    CountDownLatch latch = new CountDownLatch(results.size());
                    for (PokemonListResponse.PokemonResult pokemonResult : results) {
                        if (isRefreshing) { // Check before fetching individual Pokémon details
                            Log.d("FetchBatch", "Refreshing detected. Skipping Pokémon details.");
                            latch.countDown(); // Ensure latch decrements even when skipping
                            continue;
                        }
                    }
                    for (PokemonListResponse.PokemonResult pokemonResult : results) {
                        api.getPokemon(pokemonResult.getName()).enqueue(new Callback<Pokemon>() {
                            @Override
                            public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    Pokemon detailedPokemon = response.body();
                                    pokemonResult.setId(detailedPokemon.getId());

                                    // Check if the Pokémon matches the filter criteria
                                    if (shouldDisplayPokemon(detailedPokemon, type,seeAll)) {
                                        synchronized (filteredResults) {
                                            filteredResults.add(pokemonResult); // Add to the filtered list safely
                                        }
                                    }
                                }
                                latch.countDown(); // Signal that this Pokémon has been processed
                            }

                            @Override
                            public void onFailure(Call<Pokemon> call, Throwable t) {
                                Log.e("API_ERROR", "Failed to fetch details for " + pokemonResult.getName());
                                latch.countDown(); // Signal failure
                            }
                        });
                    }

                    // Wait for all Pokémon in the batch to be processed before updating the UI
                    fetchThread = new Thread(() -> {
                        try {
                            latch.await();
                            if (Thread.currentThread().isInterrupted()) {
                                Log.d("FetchThread", "Thread interrupted, stopping...");
                                return; // Exit gracefully if interrupted
                            }// Wait for all Pokémon in the batch to finish processing
                            if (isRefreshing) {
                                isLoading = false;
                                return;
                            }
                            currentOffset += PAGE_SIZE; // Increment offset for the next batch
                            isLoading = false;

                            // Sort the filtered Pokémon list by ID to ensure order
                            filteredResults.sort(Comparator.comparingInt(PokemonListResponse.PokemonResult::getId));

                            // Add filteredResults to the main pokemonList
                            synchronized (pokemonList) {
                                pokemonList.addAll(filteredResults);
                            }

                            // Update the UI on the main thread
                            activity.runOnUiThread(() -> {
                                // Add views only for the new batch
                                for (PokemonListResponse.PokemonResult pokemon : filteredResults) {
                                    Log.d("The fuck i show", "" + pokemon.getId() + " offset" + currentOffset);
                                    addPokemonView(pokemon);
                                }

                                // Fetch the next batch if there are more Pokémon to load
                                if (currentOffset < totalPokemons) {
                                    fetchPokemonBatch(api, searchText, seeAll, type, game);
                                }
                                else{
                                    Toast.makeText(context, "All data loaded", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } catch (InterruptedException e) {
                            Log.e("ThreadError", "Batch processing interrupted");
                        }
                    });
                    fetchThread.start();

                } else {
                    Log.e("API_ERROR", "Failed to fetch Pokémon list");
                    isLoading = false;
                }
            }

            @Override
            public void onFailure(Call<PokemonListResponse> call, Throwable t) {
                Log.e("API_ERROR", "Failed to fetch Pokémon list");
                isLoading = false;
            }
        });
    }


    private boolean shouldDisplayPokemon(Pokemon detailedPokemon, String type,List<String> seeAll) {
        boolean okType=false;
        boolean okAll=true;
        if (type == null || type.equalsIgnoreCase("All")) {
            okType = true;
        }
        else{
            List<Pokemon.TypeSlot> pokemonTypes = detailedPokemon.getTypes();

            // Handle null types more gracefully
            if (pokemonTypes == null || pokemonTypes.isEmpty()) {
                Log.d("PokemonTypes", "Pokemon: " + detailedPokemon.getName() + " has no types.");
                okType=false;  // If there are no types, skip this Pokémon.
            }

            // Now check if the Pokémon matches the requested type
            for (Pokemon.TypeSlot typeSlot : pokemonTypes) {
                if (type.equalsIgnoreCase(typeSlot.getType().getName())) {
                    Log.d("Show","Show");
                    okType=true;
                }
            }
        }
        if(seeAll!=null) {
            String pokemonName = detailedPokemon.getName();
            if (pokemonName!=null && seeAll.contains(pokemonName))
                okAll = true;
            else
                okAll = false;
        }
        else{
            okAll=true;
        }
        if(okType && okAll)
            return true;
        return false;  // If no match was found for the specified type
    }

    private void addPokemonView(PokemonListResponse.PokemonResult pokemon) {
        // Dynamically inflate the layout for each Pokémon
        View pokemonView = LayoutInflater.from(context).inflate(R.layout.pokemon_item, pokemonContainer, false);
        String originalName = pokemon.getName();
        String filteredName;

        // Filter the Pokémon name if it contains certain substrings
        if (originalName.contains("-incarnate") ||
                originalName.contains("-therian") ||
                originalName.contains("-two-segment")) {
            filteredName = originalName.split("-")[0]; // Take only the first part
        } else {
            filteredName = originalName; // Keep the full name for other cases
        }

        // Set layout parameters for GridLayout
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.width = 0; // This will distribute space equally in the GridLayout
        layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // Distribute space evenly
        layoutParams.setMargins(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
        pokemonView.setLayoutParams(layoutParams);

        // Set Pokémon name
        TextView pokemonName = pokemonView.findViewById(R.id.pokemon_info);
        pokemonName.setText(filteredName);

        // Set Pokémon image
        ImageView pokemonImage = pokemonView.findViewById(R.id.pokemon_image);
        Glide.with(context)
                .load(pokemon.getShinyImageUrl())
                .into(pokemonImage);

        // Add the Pokémon view to the GridLayout
        pokemonContainer.addView(pokemonView);
    }

    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    public synchronized void resetFetcher() {
        // Interrupt the ongoing fetch thread if it's running
        if (fetchThread != null && fetchThread.isAlive()) {
            fetchThread.interrupt();
            try {
                fetchThread.join();  // Wait for the thread to finish its current work
            } catch (InterruptedException e) {
                Log.e("ThreadError", "Failed to join interrupted thread");
            }
        }

        // Now reset everything
        currentOffset = 0;  // Reset the offset
        isLoading = false;  // Reset the loading state
        pokemonList.clear();  // Clear the current list if needed

        // Optionally clear the UI if you want to reset it
        pokemonContainer.removeAllViews();

        // Log reset to verify
        Log.d("ResetFetcher", "Reset complete. currentOffset = " + currentOffset);
        isRefreshing = true;
    }


}