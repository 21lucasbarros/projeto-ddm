package br.unisanta.projetoddm.ui.recommendations

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.unisanta.projetoddm.R
import br.unisanta.projetoddm.data.model.MovieDoc
import br.unisanta.projetoddm.data.model.RatingDoc
import br.unisanta.projetoddm.data.recommendation.RecommendationService
import br.unisanta.projetoddm.data.repository.AuthRepository
import br.unisanta.projetoddm.data.repository.MovieRepository
import br.unisanta.projetoddm.data.repository.RatingRepository
import br.unisanta.projetoddm.ui.BaseActivity
import br.unisanta.projetoddm.ui.movie.MovieDetailActivity
import com.google.android.material.appbar.MaterialToolbar

class RecommendationsActivity : BaseActivity() {

    private val authRepository = AuthRepository()
    private val movieRepository = MovieRepository()
    private val ratingRepository = RatingRepository()
    private val recommendationService = RecommendationService()

    private lateinit var adapter: RecommendationAdapter
    private lateinit var progress: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var tvSummary: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommendations)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        progress = findViewById(R.id.progress)
        tvEmpty = findViewById(R.id.tvEmpty)
        tvSummary = findViewById(R.id.tvSummary)

        adapter = RecommendationAdapter { item ->
            val intent = Intent(this, MovieDetailActivity::class.java)
            intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, item.movie.id)
            startActivity(intent)
        }
        val recycler = findViewById<RecyclerView>(R.id.rvRecommendations)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
    }

    // onResume: se o usuário avaliar um filme e voltar, a lista é recalculada
    override fun onResume() {
        super.onResume()
        carregarRecomendacoes()
    }

    // 1. nome do usuário => 2. todos os filmes => 3. avaliações do usuário => 4. motor de grafo
    private fun carregarRecomendacoes() {
        val userId = authRepository.getCurrentUserId()
        progress.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE

        authRepository.getUserName { userName ->
            movieRepository.getAllMovies(
                onSuccess = { movies ->
                    ratingRepository.getRatingsByUser(userId,
                        onSuccess = { ratings ->
                            calcular(userId, userName, movies, ratings)
                        },
                        onError = { mensagem -> mostrarErro(mensagem) }
                    )
                },
                onError = { mensagem -> mostrarErro(mensagem) }
            )
        }
    }

    private fun calcular(userId: String, userName: String, movies: List<MovieDoc>, ratings: List<RatingDoc>) {
        val items = recommendationService.recommend(userId, userName, movies, ratings, 10)

        var curtidos = 0
        for (rating in ratings) {
            if (rating.score >= 4) curtidos++
        }
        tvSummary.text = "Baseado em " + curtidos + " filme(s) que você avaliou com 4 ou 5 estrelas."

        progress.visibility = View.GONE
        adapter.submitList(items)
        tvEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun mostrarErro(mensagem: String) {
        progress.visibility = View.GONE
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show()
    }
}