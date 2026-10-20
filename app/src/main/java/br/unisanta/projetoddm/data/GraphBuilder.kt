package br.unisanta.projetoddm.data

import br.unisanta.projetoddm.data.model.MovieDoc
import br.unisanta.projetoddm.data.model.RatingDoc
import br.unisanta.projetoddm.engine.model.CastMember
import br.unisanta.projetoddm.engine.model.Movie
import br.unisanta.projetoddm.engine.model.RoleType
import br.unisanta.projetoddm.engine.model.UserProfile

object GraphBuilder {

    // nota minima para o filme contar como curtido
    private const val NOTA_MINIMA_CURTIDO = 4;

    // quantos filmes curtidos do mesmo director/ator para virar favorito
    private const val MINIMO_PARA_FAVORITO = 2;

    fun toMovie(doc: MovieDoc): Movie {
        val genres = HashSet<String>();
        for(genre in doc.genres) {
            genres.add(genre);
        }

        val cast = ArrayList<CastMember>();
        for (castDoc in doc.cast) {
            cast.add(CastMember(castDoc.actorName, toRoleType(castDoc.role)));
        }

        // se não tiver diretor, usa um valor único para não ligar todos os filmes sem diretor entre si
        var director = doc.director
        if (director.isBlank()) {
            director = "sem-diretor-" + doc.id;
        }
        return Movie(doc.id, doc.title, director, genres, cast, doc.franchise, doc.releaseYear, doc.rating, );
    }

    private fun toRoleType(role: String): RoleType {
        if (role == "PROTAGONIST") return RoleType.PROTAGONIST
        if (role == "CAMEO") return RoleType.CAMEO
        return RoleType.SUPPORTING
    }

    fun buildUserProfile(
        userName: String,
        userRatings: List<RatingDoc>,
        moviesById: Map<String, Movie>
    ): UserProfile {
        val profile = UserProfile(userName)

        val contagemDiretores = HashMap<String, Int>()
        val contagemAtores = HashMap<String, Int>()

        for (rating in userRatings) {
            if (rating.score < NOTA_MINIMA_CURTIDO) continue

            val movie = moviesById[rating.movieId] ?: continue
            profile.addLikedMovie(movie)

            val diretor = movie.director
            contagemDiretores[diretor] = (contagemDiretores[diretor] ?: 0) + 1

            for (member in movie.cast) {
                val ator = member.actorName
                contagemAtores[ator] = (contagemAtores[ator] ?: 0) + 1
            }
        }

        // diretor/ator que aparece em vários filmes curtidos vira favorito
        for ((diretor, vezes) in contagemDiretores) {
            if (vezes >= MINIMO_PARA_FAVORITO) {
                profile.addFavoriteDirector(diretor)
            }
        }
        for ((ator, vezes) in contagemAtores) {
            if (vezes >= MINIMO_PARA_FAVORITO) {
                profile.addFavoriteActor(ator)
            }
        }

        return profile
    }
}