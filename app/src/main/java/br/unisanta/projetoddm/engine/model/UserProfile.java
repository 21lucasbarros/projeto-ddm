package br.unisanta.projetoddm.engine.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import br.unisanta.projetoddm.engine.graph.ConnectionType;

public class UserProfile {
    private String name;
    private List<Movie> likedMovies = new ArrayList<>();
    private Set<String> favoritedDirectors = new HashSet<>();
    private Set<String> favoriteActors = new HashSet<>();
    private Map<ConnectionType, Double> connectionTypeMultipliers = new HashMap<>();

    private static final double FAVORITE_BOOST = 1.6;

    public UserProfile(String name) {
        this.name = name;

        for(ConnectionType type : ConnectionType.values()) {
            connectionTypeMultipliers.put(type, 1.0);
        }
    }

    public String getName() {
        return name;
    }

    public void addLikedMovies(Movie movie) {
        if(!likedMovies.contains(movie)) {
            likedMovies.add(movie);
        }
    }

    public void addFavoriteDirector(String director) {
        favoritedDirectors.add(director.toLowerCase());
    }

    public void addFavoriteActor(String actor) {
        favoriteActors.add(actor.toLowerCase());
    }

    public void setConnectionTypeMultiplier(ConnectionType type, double multiplier) {
        connectionTypeMultipliers.put(type, multiplier);
    }

    public List<Movie> getLikedMovies() {
        return likedMovies;
    }

    public boolean isFavoriteDirector(String director) {
        if (director == null) return false;

        return favoritedDirectors.contains(director.toLowerCase());
    }

    public boolean isFavoriteActor(String actor) {
        if (actor == null) return false;

        return favoriteActors.contains(actor.toLowerCase());
    }

    public double getConnectionTypeMultiplier(ConnectionType type) {
        Double multiplier = connectionTypeMultipliers.get(type);
        if (multiplier != null) {
            return multiplier;
        }
        return 1.0;
    }

    public double getFavoriteBoost() {
        return FAVORITE_BOOST;
    }
}
