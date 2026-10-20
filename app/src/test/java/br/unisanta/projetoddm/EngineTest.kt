package br.unisanta.projetoddm

import br.unisanta.projetoddm.engine.graph.ConnectionType
import br.unisanta.projetoddm.engine.graph.MovieGraph
import br.unisanta.projetoddm.engine.model.CastMember
import br.unisanta.projetoddm.engine.model.Movie
import br.unisanta.projetoddm.engine.model.RoleType
import br.unisanta.projetoddm.engine.model.UserProfile
import br.unisanta.projetoddm.engine.recommendation.RecommendationEngine
import br.unisanta.projetoddm.engine.recommendation.RecommendationResult
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EngineTest {

    @Test
    fun recomendaFilmesParaUsuarioQueCurtiuNolanEDuna() {
        val filmes = montarFilmes()
        val graph = MovieGraph(filmes)

        val usuario = UserProfile("Lucas")
        usuario.addLikedMovie(buscar(filmes, "m1"))   // Inception
        usuario.addLikedMovie(buscar(filmes, "m2"))   // The Dark Knight
        usuario.addLikedMovie(buscar(filmes, "m5"))   // Dune
        usuario.addFavoriteDirector("Christopher Nolan")
        usuario.setConnectionTypeMultiplier(ConnectionType.GENRE, 1.2)

        val resultados = RecommendationEngine(graph).recommend(usuario, 5)

        imprimirRelatorio(usuario, resultados)

        // tem que recomendar alguma coisa
        assertFalse(resultados.isEmpty())

        // nunca recomenda um filme que o usuário já curtiu
        for (resultado in resultados) {
            assertFalse(usuario.likedMovies.contains(resultado.movie))
        }

        // Dune: Part Two (mesma franquia) e Interstellar (Nolan, diretor favorito)
        // têm que estar no top 5
        val idsRecomendados = ArrayList<String>()
        for (resultado in resultados) {
            idsRecomendados.add(resultado.movie.id)
        }
        assertTrue(idsRecomendados.contains("m6"))
        assertTrue(idsRecomendados.contains("m3"))
    }

    // relatorio

    private fun imprimirRelatorio(usuario: UserProfile, resultados: List<RecommendationResult>) {
        println("===================================================")
        println(" Graph Score - Recomendação de filmes por grafo")
        println("===================================================")
        println()
        println("Usuário: " + usuario.name)
        println("Filmes curtidos:")
        for (filme in usuario.likedMovies) {
            println("   - " + filme)
        }
        println()
        println("---------------------------------------------------")
        println(" Top " + resultados.size + " recomendações")
        println("---------------------------------------------------")

        var posicao = 1
        for (resultado in resultados) {
            println()
            println(posicao.toString() + ". " + resultado.movie + "  |  compatibilidade: " + resultado.score)
            println("   Motivos:")
            for (motivo in resultado.reasons) {
                println("   - " + motivo)
            }
            posicao++
        }
        println()
        println("===================================================")
    }

    // dados de teste

    private fun buscar(filmes: List<Movie>, id: String): Movie {
        for (filme in filmes) {
            if (filme.id == id) return filme
        }
        throw IllegalArgumentException("Filme não encontrado: " + id)
    }

    private fun filme(
        id: String, titulo: String, diretor: String,
        generos: Set<String>, elenco: List<CastMember>,
        franquia: String?, nota: Double, ano: Int
    ): Movie {
        return Movie(id, titulo, diretor, HashSet(generos), ArrayList(elenco), franquia, nota, ano)
    }

    private fun montarFilmes(): List<Movie> {
        val p = RoleType.PROTAGONIST
        val s = RoleType.SUPPORTING
        val c = RoleType.CAMEO

        val filmes = ArrayList<Movie>()

        filmes.add(filme("m1", "Inception", "Christopher Nolan",
            setOf("Ficção Científica", "Ação"),
            listOf(CastMember("Leonardo DiCaprio", p), CastMember("Tom Hardy", s), CastMember("Michael Caine", c)),
            null, 8.8, 2010))

        filmes.add(filme("m2", "The Dark Knight", "Christopher Nolan",
            setOf("Ação", "Drama"),
            listOf(CastMember("Christian Bale", p), CastMember("Heath Ledger", p), CastMember("Michael Caine", s)),
            "The Dark Knight Trilogy", 9.0, 2008))

        filmes.add(filme("m3", "Interstellar", "Christopher Nolan",
            setOf("Ficção Científica", "Drama"),
            listOf(CastMember("Matthew McConaughey", p), CastMember("Anne Hathaway", s), CastMember("Michael Caine", s)),
            null, 8.6, 2014))

        filmes.add(filme("m4", "Oppenheimer", "Christopher Nolan",
            setOf("Drama", "História"),
            listOf(CastMember("Cillian Murphy", p), CastMember("Robert Downey Jr.", s)),
            null, 9.0, 2023))

        filmes.add(filme("m5", "Dune", "Denis Villeneuve",
            setOf("Ficção Científica", "Aventura"),
            listOf(CastMember("Timothée Chalamet", p), CastMember("Zendaya", s), CastMember("Josh Brolin", s)),
            "Dune", 8.0, 2021))

        filmes.add(filme("m6", "Dune: Part Two", "Denis Villeneuve",
            setOf("Ficção Científica", "Aventura"),
            listOf(CastMember("Timothée Chalamet", p), CastMember("Zendaya", p), CastMember("Josh Brolin", s)),
            "Dune", 8.7, 2024))

        filmes.add(filme("m7", "Blade Runner 2049", "Denis Villeneuve",
            setOf("Ficção Científica", "Drama"),
            listOf(CastMember("Ryan Gosling", p), CastMember("Harrison Ford", s)),
            null, 8.7, 2017))

        filmes.add(filme("m8", "Alien", "Ridley Scott",
            setOf("Ficção Científica", "Terror"),
            listOf(CastMember("Sigourney Weaver", p), CastMember("Tom Skerritt", s)),
            "Alien", 8.5, 1979))

        filmes.add(filme("m9", "Tenet", "Christopher Nolan",
            setOf("Ficção Científica", "Ação"),
            listOf(CastMember("John David Washington", p), CastMember("Robert Pattinson", s)),
            null, 7.3, 2020))

        filmes.add(filme("m10", "Whiplash", "Damien Chazelle",
            setOf("Drama", "Música"),
            listOf(CastMember("Miles Teller", p), CastMember("J.K. Simmons", s)),
            null, 8.5, 2014))

        filmes.add(filme("m12", "La La Land", "Damien Chazelle",
            setOf("Drama", "Romance"),
            listOf(CastMember("Ryan Gosling", p), CastMember("Emma Stone", p)),
            null, 8.0, 2016))

        filmes.add(filme("m13", "Se7en", "David Fincher",
            setOf("Crime", "Suspense"),
            listOf(CastMember("Brad Pitt", p), CastMember("Morgan Freeman", p)),
            null, 8.6, 1995))

        filmes.add(filme("m15", "Titanic", "James Cameron",
            setOf("Drama", "Romance"),
            listOf(CastMember("Leonardo DiCaprio", p), CastMember("Kate Winslet", p)),
            null, 7.9, 1997))

        filmes.add(filme("m16", "Avatar", "James Cameron",
            setOf("Ficção Científica", "Aventura"),
            listOf(CastMember("Sam Worthington", p), CastMember("Zoe Saldana", p)),
            "Avatar", 7.9, 2009))

        filmes.add(filme("m17", "Parasite", "Bong Joon-ho",
            setOf("Drama", "Suspense"),
            listOf(CastMember("Song Kang-ho", p)),
            null, 8.5, 2019))

        filmes.add(filme("m21", "Mickey 17", "Bong Joon-ho",
            setOf("Ficção Científica", "Drama"),
            listOf(CastMember("Robert Pattinson", p), CastMember("Steven Yeun", p)),
            null, 7.5, 2025))

        return filmes
    }
}