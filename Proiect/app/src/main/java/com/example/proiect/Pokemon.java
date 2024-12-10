package com.example.proiect;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Pokemon {
   @SerializedName("name")
    private String name;
    @SerializedName("sprites")
    private Sprites sprites;
    @SerializedName("types")
    public List<TypeSlot> types;
    @SerializedName("id")
    public int id;
    // Getters
    public String getName() {
        return name;
    }
    public Sprites getSprites() {
        return sprites;
    }
    public int getId(){return id;}
    public List<TypeSlot> getTypes() {return types;}

    // Helper methods to get type1 and type2
    public String getType1() {
        if (types != null && types.size() > 0) {
            return types.get(0).getType().getName();
        }
        return null; // No type1 available
    }

    public String getType2() {
        if (types != null && types.size() > 1) {
            return types.get(1).getType().getName();
        }
        return null; // No type2 available
    }
    public static class Sprites {
        @SerializedName("front_shiny")
        private String frontShiny;

        public String getFrontShiny() {
            return frontShiny;
        }

    }

    public static class TypeSlot {
        @SerializedName("slot")
        private int slot;

        @SerializedName("type")
        private Type type;

        public int getSlot() {
            return slot;
        }

        public Type getType() {
            return type;
        }
    }

    public static class Type {
        @SerializedName("name")
        private String name;

        public String getName() {
            return name;
        }
    }
}
