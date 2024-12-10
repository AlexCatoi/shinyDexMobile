package com.example.proiect;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class PokemonListResponse {
    private List<PokemonResult> results;

    public List<PokemonResult> getResults() {
        return results;
    }

    public static class PokemonResult {
        private String name;
        private String url;
        private int id;
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        



        public String getImageUrl() {
            // Extract Pokémon ID from the URL
            String id = url.split("pokemon/")[1].replace("/", "");
            return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + id + ".png";
        }
        public String getShinyImageUrl() {
            // Extract Pokémon ID from the URL
            String id = url.split("pokemon/")[1].replace("/", "");
            return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/shiny/" + id + ".png";
        }


    }
}
