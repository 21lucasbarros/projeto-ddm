package br.unisanta.projetoddm.engine.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.unisanta.projetoddm.engine.model.CastMember;
import br.unisanta.projetoddm.engine.model.Movie;

public class MovieGraph {
    private List<Movie> movies;

    private Map<String, List<Movie>> byDirector = new HashMap<>();
    private Map<String, List<Movie>> byActor = new HashMap<>();
    private Map<String, List<Movie>> byGenre = new HashMap<>();
    private Map<String, List<Movie>> byFranchise = new HashMap<>();
    private Map<Integer, List<Movie>> byEra = new HashMap<>();

    public MovieGraph(List<Movie> movies) {
        this.movies = movies;
        montarIndices();
    }

    private void adicionarNoIndice(Map<String, List<Movie>> mapa, String chave, Movie movie) {
        List<Movie> lista = mapa.get(chave);
        if (lista == null) {
            lista = new ArrayList<>();
            mapa.put(chave, lista);
        }
        lista.add(movie);
    }

    private void adicionarNoIndiceEra(int era, Movie movie) {
        List<Movie> lista = byEra.get(era);
        if (lista == null) {
            lista = new ArrayList<>();
            byEra.put(era, lista);
        }
        lista.add(movie);
    }

    private void montarIndices() {
        for (Movie movie : movies) {
            adicionarNoIndice(byDirector, movie.getDirector().toLowerCase(), movie);

            for (CastMember cast : movie.getCast()) {
                adicionarNoIndice(byActor, cast.getActorName().toLowerCase(), movie);
            }
            for (String genre : movie.getGenres()) {
                adicionarNoIndice(byGenre, genre.toLowerCase(), movie);
            }
            if (movie.getFranchise() != null) {
                adicionarNoIndice(byFranchise, movie.getFranchise().toLowerCase(), movie);
            }

            adicionarNoIndiceEra(movie.getEra(), movie);
        }
    }

    private List<Edge> conectarPorDiretor(Movie source) {
        List<Edge> edges = new ArrayList<>();
        String chave = source.getDirector().toLowerCase();
        List<Movie> candidatos = byDirector.get(chave);

        if (candidatos == null) {
            return edges;
        }

        for (Movie candidato : candidatos) {
            if (candidato.equals(source)) {
                continue;
            }
            String reason = "mesmo diretor (" + source.getDirector() + ")";
            edges.add(new Edge(candidato, ConnectionType.DIRECTOR, ConnectionType.DIRECTOR.getBaseWeight(), reason, source.getDirector()));
        }
        return edges;
    }

    private List<Edge> conectarPorAtores(Movie source) {
        List<Edge> edges = new ArrayList<>();
        for (CastMember member : source.getCast()) {
            List<Movie> candidatos = byActor.get(member.getActorName().toLowerCase());
            if (candidatos == null) {
                continue;
            }
            for (Movie candidato : candidatos) {
                if (candidato.equals(source)) {
                    continue;
                }
                CastMember outroMember = candidato.findCastMember(member.getActorName());
                if (outroMember == null) {
                    continue;
                }
                double roleFactor = Math.min(member.getRole().getRelevance(), outroMember.getRole().getRelevance());
                double weight = ConnectionType.ACTOR.getBaseWeight() * roleFactor;

                String reason = "elenco em comum: " + member.getActorName();
                edges.add(new Edge(candidato, ConnectionType.ACTOR, weight, reason, member.getActorName()));
            }
        }
        return edges;
    }

    private List<Edge> conectarPorGeneros(Movie source) {
        List<Edge> edges = new ArrayList<>();

        for (String genre : source.getGenres()) {
            List<Movie> candidatos = byGenre.get(genre.toLowerCase());
            if (candidatos == null) continue;

            for (Movie candidato : candidatos) {
                if (candidato.equals(source)) continue;

                String reason = "mesmo gênero: " + genre;
                edges.add(new Edge(candidato, ConnectionType.GENRE, ConnectionType.GENRE.getBaseWeight(), reason, genre));
            }
        }
        return edges;
    }

    private List<Edge> conectarPorFranquia(Movie source) {
        List<Edge> edges = new ArrayList<>();
        if (source.getFranchise() == null) return edges;

        List<Movie> candidatos = byFranchise.get(source.getFranchise().toLowerCase());
        if (candidatos == null) return edges;

        for (Movie candidato : candidatos) {
            if (candidato.equals(source)) continue;

            String reason = "mesma franquia: " + source.getFranchise();
            edges.add(new Edge(candidato, ConnectionType.FRANCHISE, ConnectionType.FRANCHISE.getBaseWeight(), reason, source.getFranchise()));
        }
        return edges;
    }

    private List<Edge> conectarPorEpoca(Movie source) {
        List<Edge> edges = new ArrayList<>();
        List<Movie> candidatos = byEra.get(source.getEra());

        if (candidatos == null) return edges;

        for (Movie candidato : candidatos) {
            if (candidato.equals(source)) continue;

            String reason = "mesma época: década de " + source.getEra();
            edges.add(new Edge(candidato, ConnectionType.ERA, ConnectionType.ERA.getBaseWeight(), reason, String.valueOf(source.getEra())));
        }
        return edges;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public List<Edge> getConnections(Movie source) {
        List<Edge> edges = new ArrayList<>();

        edges.addAll(conectarPorDiretor(source));
        edges.addAll(conectarPorAtores(source));
        edges.addAll(conectarPorGeneros(source));
        edges.addAll(conectarPorFranquia(source));
        edges.addAll(conectarPorEpoca(source));

        return edges;
    }
}
