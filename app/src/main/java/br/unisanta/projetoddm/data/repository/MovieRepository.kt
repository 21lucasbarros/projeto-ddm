package br.unisanta.projetoddm.data.repository

import br.unisanta.projetoddm.data.model.MovieDoc
import com.google.firebase.firestore.FirebaseFirestore

class MovieRepository {

    private val db = FirebaseFirestore.getInstance()
    private val movies = db.collection("movies")

    fun getAllMovies(onSuccess: (List<MovieDoc>) -> Unit, onError: (String) -> Unit) {
        movies.orderBy("title").get()
            .addOnSuccessListener { snapshot ->
                val lista = ArrayList<MovieDoc>()
                for (doc in snapshot.documents) {
                    val movie = doc.toObject(MovieDoc::class.java)
                    if (movie != null) {
                        lista.add(movie)
                    }
                }
                onSuccess(lista)
            }
            .addOnFailureListener { e -> onError("Erro ao carregar filmes: " + e.message) }
    }

    fun getMovie(movieId: String, onSuccess: (MovieDoc) -> Unit, onError: (String) -> Unit) {
        movies.document(movieId).get()
            .addOnSuccessListener { snapshot ->
                val movie = snapshot.toObject(MovieDoc::class.java)
                if (movie == null) {
                    onError("Filme não encontrado.")
                } else {
                    onSuccess(movie)
                }
            }
            .addOnFailureListener { e -> onError("Erro ao carregar o filme: " + e.message) }
    }

    // grava varios filmes de uma vez, vamos usar isso para popular no começo
    fun seedMovies(lista: List<MovieDoc>, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val batch = db.batch()
        for (movie in lista) {
            batch.set(movies.document(movie.id), movie)
        }
        batch.commit()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError("Erro ao popular o DB: " + e.message) }
    }

    // salva só o campo posterUrl de um filme
    fun updatePoster(movieId: String, posterUrl: String) {
        movies.document(movieId).update("posterUrl", posterUrl)
    }
}