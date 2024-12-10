package com.example.proiect;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.fragment.app.FragmentTransaction;
import androidx.constraintlayout.widget.ConstraintLayout;

public class PokemonListFragment extends Fragment {
    private GridLayout pokemonContainer;
    private ProgressBar progressBar;
    private TextView progressPercent;
    private View fragmentContainer;
    private ScrollView scrollView;
    private PokemonFetcher util;
    private JsonFileHelper jsonhelp;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment layout
        View rootView = inflater.inflate(R.layout.fragment_pokemon_list, container, false);
        // Bind views
        pokemonContainer = rootView.findViewById(R.id.pokemon_container);
        ImageButton menuButton = rootView.findViewById(R.id.menu_button);
        fragmentContainer = rootView.findViewById(R.id.fragment_container_filter);
        progressBar = rootView.findViewById(R.id.progress_bar);
        progressPercent = rootView.findViewById(R.id.progress_text);
        scrollView = rootView.findViewById(R.id.scroll_view);

        menuButton.setOnClickListener(v -> showPopupMenu(v));
        // Start fetching Pokémon data
        util=new PokemonFetcher(getContext(),getActivity(),pokemonContainer);
        jsonhelp=new JsonFileHelper();
        fetchAllPokemon(null,null,null,null);
        return rootView;
    }


    private void fetchAllPokemon(String searchText, String seeAll, String type, String game) {
        Toast.makeText(getContext(), "Wait for the data to be loaded", Toast.LENGTH_SHORT).show();
        PokemonAPI api = ApiClient.getClient().create(PokemonAPI.class);
        int numberOfColumns = getOptimalColumnCount();
        pokemonContainer.setColumnCount(numberOfColumns);

        if(util!=null)
            util.resetFetcher();
        if(seeAll==null)
            util.fetchPokemonBatch(api, searchText, null, type, game);
        else{
            List<String> pokes_caught=jsonhelp.readJsonFile(getContext(),"pokemon_caught_data.json","name");
            Log.d("pokes",""+pokes_caught);
            util.fetchPokemonBatch(api, searchText, pokes_caught, type, game);
        }


    }

    // Helper method to convert dp to px
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private int getOptimalColumnCount() {
        int itemWidthDp = 120; // Approximate width of each item in dp
        float density = getResources().getDisplayMetrics().density;
        int itemWidthPx = Math.round(itemWidthDp * density);
        int screenWidthPx = getResources().getDisplayMetrics().widthPixels;

        // Add margins to the item's width
        int totalItemWidthPx = itemWidthPx + dpToPx(16); // Approx. 8dp on both sides
        return Math.max(screenWidthPx / totalItemWidthPx, 1); // Prevent zero columns
    }



    private void addFilterFragment() {
        // Create a new instance of the filter fragment
        fragment_PokemonFilter filterFragment = new fragment_PokemonFilter();
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) scrollView.getLayoutParams();

        params.topToBottom = R.id.fragment_container_filter;
        scrollView.setLayoutParams(params);
        scrollView.requestLayout();
        // Begin the fragment transaction
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

        // Add the fragment to the container
        transaction.replace(R.id.fragment_container_filter, filterFragment);

        // Optionally, add the transaction to the back stack if you want to enable back navigation
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    private void removeFilterFragment() {
        // Get the currently added fragment
        Fragment filterFragment = getParentFragmentManager().findFragmentById(R.id.fragment_container_filter);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) scrollView.getLayoutParams();
        params.topToBottom = R.id.menu_button;
        scrollView.setLayoutParams(params);
        scrollView.requestLayout();
        // If the fragment is not null, remove it
        if (filterFragment != null) {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.remove(filterFragment);
            transaction.commit();
        }
    }

    private void showPopupMenu(View anchor) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), anchor);
        popupMenu.getMenuInflater().inflate(R.menu.main_menu, popupMenu.getMenu());

        // Handle menu item clicks
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_progress) {
                removeFilterFragment();
                progressBar.setVisibility(View.VISIBLE);
                progressPercent.setVisibility(View.VISIBLE);
                return true;
            } else if (item.getItemId() == R.id.menu_filters) {
                addFilterFragment();
                progressBar.setVisibility(View.GONE);
                progressPercent.setVisibility(View.GONE);
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    public void updateFilters(String type,String seeAll) {
        fetchAllPokemon(null, seeAll, type, null);
    }


}

