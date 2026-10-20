package br.unisanta.projetoddm.data.model

import com.google.firebase.firestore.DocumentId

data class MovieDoc(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val director: String = "",
    val genres: List<String> = emptyList(),
    val cast: List<CastDoc> = emptyList(),
    val franchise: String? = null,
    val rating: Double = 0.0,
    val releaseYear: Int = 0,
    val posterUrl: String = ""
)

data class CastDoc(
    val actorName: String = "",
    val role: String = "SUPPORTING" // "PROTAGONIST", "SUPPORTING" ou "CAMEO"
)
