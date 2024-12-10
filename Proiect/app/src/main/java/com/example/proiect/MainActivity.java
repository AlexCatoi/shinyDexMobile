package com.example.proiect;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            // Load the default fragment
            loadFragment(new PokemonListFragment());
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if(item.getItemId()==R.id.full_pokemon_list) {
                    selectedFragment = new PokemonListFragment();
            }
            else if(item.getItemId()==R.id.nav_add_pokemon){
                selectedFragment=new AddPokemonFragment();
            }
            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }

            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    public void applyFilters(String searchText, String seeAll, String type, String game, int generation) {
        PokemonListFragment mainFragment = (PokemonListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        Log.d("MainActivity", "applyFilters called with type: " + type);
        if (mainFragment == null) {
            Log.e("MainActivity", "PokemonListFragment is null. Ensure it is properly added to the container.");
        } else {
            mainFragment.updateFilters(type,seeAll);
        }
    }

}

