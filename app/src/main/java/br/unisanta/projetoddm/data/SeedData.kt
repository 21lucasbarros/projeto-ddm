package br.unisanta.projetoddm.data

import br.unisanta.projetoddm.data.model.CastDoc
import br.unisanta.projetoddm.data.model.MovieDoc

object SeedData {

    private const val P = "PROTAGONIST"
    private const val S = "SUPPORTING"
    private const val C = "CAMEO"

    private fun ator(nome: String, papel: String): CastDoc {
        return CastDoc(nome, papel)
    }

    fun getMovies(): List<MovieDoc> {
        val filmes = ArrayList<MovieDoc>()

        filmes.add(MovieDoc("m1", "Inception", "Christopher Nolan",
            listOf("Ficção Científica", "Ação"),
            listOf(ator("Leonardo DiCaprio", P), ator("Tom Hardy", S), ator("Michael Caine", C)),
            null, 8.8, 2010))

        filmes.add(MovieDoc("m2", "The Dark Knight", "Christopher Nolan",
            listOf("Ação", "Drama"),
            listOf(ator("Christian Bale", P), ator("Heath Ledger", P), ator("Michael Caine", S)),
            "The Dark Knight Trilogy", 9.0, 2008))

        filmes.add(MovieDoc("m3", "Interstellar", "Christopher Nolan",
            listOf("Ficção Científica", "Drama"),
            listOf(ator("Matthew McConaughey", P), ator("Anne Hathaway", S), ator("Michael Caine", S)),
            null, 8.6, 2014))

        filmes.add(MovieDoc("m4", "Oppenheimer", "Christopher Nolan",
            listOf("Drama", "História"),
            listOf(ator("Cillian Murphy", P), ator("Robert Downey Jr.", S)),
            null, 9.0, 2023))

        filmes.add(MovieDoc("m5", "Dune", "Denis Villeneuve",
            listOf("Ficção Científica", "Aventura"),
            listOf(ator("Timothée Chalamet", P), ator("Zendaya", S), ator("Josh Brolin", S)),
            "Dune", 8.0, 2021))

        filmes.add(MovieDoc("m6", "Dune: Part Two", "Denis Villeneuve",
            listOf("Ficção Científica", "Aventura"),
            listOf(ator("Timothée Chalamet", P), ator("Zendaya", P), ator("Josh Brolin", S)),
            "Dune", 8.7, 2024))

        filmes.add(MovieDoc("m7", "Blade Runner 2049", "Denis Villeneuve",
            listOf("Ficção Científica", "Drama"),
            listOf(ator("Ryan Gosling", P), ator("Harrison Ford", S)),
            null, 8.7, 2017))

        filmes.add(MovieDoc("m8", "Alien", "Ridley Scott",
            listOf("Ficção Científica", "Terror"),
            listOf(ator("Sigourney Weaver", P), ator("Tom Skerritt", S)),
            "Alien", 8.5, 1979))

        filmes.add(MovieDoc("m9", "Tenet", "Christopher Nolan",
            listOf("Ficção Científica", "Ação"),
            listOf(ator("John David Washington", P), ator("Robert Pattinson", S)),
            null, 7.3, 2020))

        filmes.add(MovieDoc("m10", "Whiplash", "Damien Chazelle",
            listOf("Drama", "Música"),
            listOf(ator("Miles Teller", P), ator("J.K. Simmons", S)),
            null, 8.5, 2014))

        filmes.add(MovieDoc("m11", "A Rede Social", "David Fincher",
            listOf("Drama", "Biografia"),
            listOf(ator("Jesse Eisenberg", P), ator("Andrew Garfield", S)),
            null, 7.8, 2010))

        filmes.add(MovieDoc("m12", "La La Land", "Damien Chazelle",
            listOf("Drama", "Romance"),
            listOf(ator("Ryan Gosling", P), ator("Emma Stone", P)),
            null, 8.0, 2016))

        filmes.add(MovieDoc("m13", "Se7en", "David Fincher",
            listOf("Crime", "Suspense"),
            listOf(ator("Brad Pitt", P), ator("Morgan Freeman", P), ator("Kevin Spacey", S)),
            null, 8.6, 1995))

        filmes.add(MovieDoc("m14", "Zodiac", "David Fincher",
            listOf("Crime", "Suspense"),
            listOf(ator("Jake Gyllenhaal", P), ator("Mark Ruffalo", S), ator("Robert Downey Jr.", S)),
            null, 7.7, 2007))

        filmes.add(MovieDoc("m15", "Titanic", "James Cameron",
            listOf("Drama", "Romance"),
            listOf(ator("Leonardo DiCaprio", P), ator("Kate Winslet", P)),
            null, 7.9, 1997))

        filmes.add(MovieDoc("m16", "Avatar", "James Cameron",
            listOf("Ficção Científica", "Aventura"),
            listOf(ator("Sam Worthington", P), ator("Zoe Saldana", P)),
            "Avatar", 7.9, 2009))

        filmes.add(MovieDoc("m17", "Parasite", "Bong Joon-ho",
            listOf("Drama", "Suspense"),
            listOf(ator("Song Kang-ho", P)),
            null, 8.5, 2019))

        filmes.add(MovieDoc("m18", "Clube da Luta", "David Fincher",
            listOf("Drama", "Suspense"),
            listOf(ator("Edward Norton", P), ator("Brad Pitt", P)),
            null, 8.8, 1999))

        filmes.add(MovieDoc("m21", "Mickey 17", "Bong Joon-ho",
            listOf("Ficção Científica", "Drama"),
            listOf(ator("Robert Pattinson", P), ator("Steven Yeun", P)),
            null, 7.5, 2025))

        filmes.add(MovieDoc("m22", "Garota Exemplar", "David Fincher",
            listOf("Drama", "Suspense", "Crime"),
            listOf(ator("Rosamund Pike", P), ator("Ben Affleck", P)),
            null, 8.1, 2014))

        filmes.add(MovieDoc("m23", "Memórias de um Assassino", "Bong Joon-ho",
            listOf("Crime", "Drama", "Suspense"),
            listOf(ator("Song Kang-ho", P)),
            null, 8.1, 2003))

        return filmes
    }
}