package br.unisanta.projetoddm.data.model

data class RatingDoc(
    val userId: String = "",
    val movieId: String = "",
    val score: Int = 0 // 1 a 5
)
