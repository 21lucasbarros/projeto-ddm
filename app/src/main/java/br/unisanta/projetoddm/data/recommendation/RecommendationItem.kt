package br.unisanta.projetoddm.data.recommendation

import br.unisanta.projetoddm.data.model.MovieDoc

data class RecommendationItem(
    val movie: MovieDoc,
    val score: Double,
    val reasons: List<String>
)
