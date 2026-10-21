package br.unisanta.projetoddm.data.repository

import br.unisanta.projetoddm.data.model.RatingDoc
import com.google.firebase.firestore.FirebaseFirestore

class RatingRepository {

    private val ratings = FirebaseFirestore.getInstance().collection("ratings")

    // o id fixo eh "uid_movieId", se o usuário avaliar de novo, a nota é substituida
    private fun docId(userId: String, movieId: String): String {
        return userId + "_" + movieId
    }

    fun saveRating(
        userId: String,
        movieId: String,
        score: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        ratings.document(docId(userId, movieId)).set(RatingDoc(userId, movieId, score))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError("Erro ao salvar avaliação: " + e.message) }
    }

    // devolve 0 se o usuario ainda não avaliou o filme
    fun getUserRatingForMovie(userId: String, movieId: String, onResult: (Int) -> Unit) {
        ratings.document(docId(userId, movieId)).get()
            .addOnSuccessListener { snapshot ->
                val rating = snapshot.toObject(RatingDoc::class.java)
                if (rating == null) {
                    onResult(0)
                } else {
                    onResult(rating.score)
                }
            }
            .addOnFailureListener { onResult(0) }
    }

    fun getRatingsByUser(userId: String, onSuccess: (List<RatingDoc>) -> Unit, onError: (String) -> Unit) {
        ratings.whereEqualTo("userId", userId).get()
            .addOnSuccessListener { snapshot ->
                val lista = ArrayList<RatingDoc>()
                for (doc in snapshot.documents) {
                    val rating = doc.toObject(RatingDoc::class.java)
                    if (rating != null) {
                        lista.add(rating)
                    }
                }
                onSuccess(lista)
            }
            .addOnFailureListener { e -> onError("Erro ao carregar avaliações: " + e.message) }
    }
}