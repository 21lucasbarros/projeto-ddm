package br.unisanta.projetoddm.data.recommendation

import br.unisanta.projetoddm.data.GraphBuilder
import br.unisanta.projetoddm.data.model.MovieDoc
import br.unisanta.projetoddm.data.model.RatingDoc
import br.unisanta.projetoddm.engine.graph.MovieGraph
import br.unisanta.projetoddm.engine.model.Movie
import br.unisanta.projetoddm.engine.recommendation.RecommendationEngine

class RecommendationService {

    /*
     * Essa classe eh responsavel por juntar os dados dos filmes e as avaliações
     * do usuário para gerar as recomendações usando o nosso algoritmo de grafo.
     *
     * basicamente, ela pega os dados que vao vir do Firestore, monta o grafo com
     * todos os filmes, identifica quais filmes o usuario curtiu e monta o perfil
     * dele para o motor de recomendação conseguir calcular os filmes mais relevantes.
     *
     * no final, ela retorna uma lista com os filmes recomendados, suas pontuacoes
     * e os motivos da recomendacao, que é o que a tela vai usar para exibir tudo.
     *
     * se forem mexer aqui, tomem cuidado para não misturar os documentos do
     * Firestore com os modelos internos do algoritmo, porque cada um tem sua funcao.
     * o GraphBuilder faz essa conversao e também monta o perfil do usuario.
     */

    fun recommend(
        userId: String,
        userName: String,
        allMovies: List<MovieDoc>,
        allRatings: List<RatingDoc>,
        topN: Int = 10
    ): List<RecommendationItem> {

        // 1. converte os documentos do Firestore para o modelo do motor
        val moviesById = HashMap<String, Movie>()
        val docsById = HashMap<String, MovieDoc>()
        for (doc in allMovies) {
            moviesById[doc.id] = GraphBuilder.toMovie(doc)
            docsById[doc.id] = doc
        }

        // 2. monta o grafo
        val graph = MovieGraph(ArrayList(moviesById.values))

        // 3. pega só as avaliacoes deste usuário
        val userRatings = ArrayList<RatingDoc>()
        for (rating in allRatings) {
            if (rating.userId == userId) {
                userRatings.add(rating)
            }
        }

        // 4. monta o perfil
        val profile = GraphBuilder.buildUserProfile(userName, userRatings, moviesById)
        if (profile.likedMovies.isEmpty()) {
            return emptyList()   // usuário ainda não curtiu nada
        }

        // 5. roda o motor
        val engine = RecommendationEngine(graph)
        val results = engine.recommend(profile, topN)

        // 6. converte de volta para algo que a tela usa
        val items = ArrayList<RecommendationItem>()
        for (result in results) {
            val doc = docsById[result.movie.id] ?: continue
            items.add(RecommendationItem(doc, result.score, result.reasons))
        }
        return items
    }
}