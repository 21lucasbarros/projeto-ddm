package br.unisanta.projetoddm.engine.recommendation;

import java.util.List;

import br.unisanta.projetoddm.engine.model.Movie;

public class RecommendationResult {
    private Movie movie;
    private double score;
    private List<String> reasons;

    public RecommendationResult(Movie movie, double score, List<String> reasons) {
        this.movie = movie;
        this.score = score;
        this.reasons = reasons;
    }

    public Movie getMovie() {
        return movie;
    }

    public double getScore() {
        return score;
    }

    public List<String> getReasons() {
        return reasons;
    }
}
