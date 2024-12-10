package com.example.proiect;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import androidx.appcompat.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class fragment_PokemonFilter extends Fragment {


    private SearchView searchBar;
    private CheckBox checkboxFilter;
    private Spinner spinnerType;
    private Spinner spinnerGame;
    private Spinner spinnerGeneration;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View rootView = inflater.inflate(R.layout.fragment_pokemon_filter, container, false);

        // Bind views
        searchBar = rootView.findViewById(R.id.search_bar);
        checkboxFilter = rootView.findViewById(R.id.checkbox_filter);
        spinnerType = rootView.findViewById(R.id.spinner_type);
        spinnerGame = rootView.findViewById(R.id.spinner_game);
        spinnerGeneration = rootView.findViewById(R.id.spinner_generation);

        // Set up listeners
        setupListeners();

        return rootView;
    }

    private void setupListeners() {
        // Handle search bar input
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform search operation
                Toast.makeText(getContext(), "Searching for: " + query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle text change (optional)
                return false;
            }
        });

        // Handle checkbox state change
        checkboxFilter.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(getContext(), "Showing all Pokémon", Toast.LENGTH_SHORT).show();
                passFiltersToMainActivity(null, null, null, null,0);
            } else {
                Toast.makeText(getContext(), "Showing caught pokemon", Toast.LENGTH_SHORT).show();
                passFiltersToMainActivity(null, "y", null, null,0);
            }
        });

        // Handle spinner selection
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = parent.getItemAtPosition(position).toString();
                Log.d("FilterFragment", "Selected type: " + selectedType);
                if(selectedType.contains("All"))
                    passFiltersToMainActivity(null, null, null, null,0);
                else
                    passFiltersToMainActivity(null,null,selectedType,null,0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: handle no selection
            }
        });

        spinnerGame.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedGame = parent.getItemAtPosition(position).toString();
                Toast.makeText(getContext(), "Game selected: " + selectedGame, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: handle no selection
            }
        });

        spinnerGeneration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedGeneration = parent.getItemAtPosition(position).toString();
                Toast.makeText(getContext(), "Generation selected: " + selectedGeneration, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: handle no selection
            }
        });
    }
    private void passFiltersToMainActivity(String searchText, String seeAll, String type, String game, int generation) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).applyFilters(searchText, seeAll, type, game,generation);
        }
    }
}