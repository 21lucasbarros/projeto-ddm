package br.unisanta.projetoddm.ui.liked

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
import br.unisanta.projetoddm.data.repository.AuthRepository
import br.unisanta.projetoddm.data.repository.MovieRepository
import br.unisanta.projetoddm.data.repository.RatingRepository
import br.unisanta.projetoddm.ui.BaseActivity
import br.unisanta.projetoddm.ui.home.MovieAdapter
import br.unisanta.projetoddm.ui.movie.MovieDetailActivity
import com.google.android.material.appbar.MaterialToolbar

class LikedMoviesActivity : BaseActivity() {

    // mesmo critério, nota 4 ou 5 conta como curtido
    private val NOTA_MINIMA_CURTIDO = 4

    private val authRepository = AuthRepository()
    private val movieRepository = MovieRepository()
    private val ratingRepository = RatingRepository()

    private lateinit var adapter: MovieAdapter
    private lateinit var progress: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var tvSummary: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liked_movies)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        progress = findViewById(R.id.progress)
        tvEmpty = findViewById(R.id.tvEmpty)
        tvSummary = findViewById(R.id.tvSummary)

        adapter = MovieAdapter { movie ->
            val intent = Intent(this, MovieDetailActivity::class.java)
            intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, movie.id)
            startActivity(intent)
        }
        val recycler = findViewById<RecyclerView>(R.id.rvLiked)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
    }

    // se o usuário mudar uma nota no detalhe e voltar, a lista atualiza
    override fun onResume() {
        super.onResume()
        carregarCurtidos()
    }

    private fun carregarCurtidos() {
        val userId = authRepository.getCurrentUserId()
        progress.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE

        ratingRepository.getRatingsByUser(userId,
            onSuccess = { ratings ->
                movieRepository.getAllMovies(
                    onSuccess = { movies -> mostrar(movies, ratings) },
                    onError = { mensagem -> mostrarErro(mensagem) }
                )
            },
            onError = { mensagem -> mostrarErro(mensagem) }
        )
    }

    private fun mostrar(movies: List<MovieDoc>, ratings: List<RatingDoc>) {
        // ids dos filmes com nota 4 ou 5
        val idsCurtidos = HashSet<String>()
        for (rating in ratings) {
            if (rating.score >= NOTA_MINIMA_CURTIDO) {
                idsCurtidos.add(rating.movieId)
            }
        }

        val curtidos = ArrayList<MovieDoc>()
        for (movie in movies) {
            if (idsCurtidos.contains(movie.id)) {
                curtidos.add(movie)
            }
        }

        progress.visibility = View.GONE
        tvSummary.text = curtidos.size.toString() + " filme(s) avaliado(s) com 4 ou 5 estrelas."
        adapter.submitList(curtidos)
        tvEmpty.visibility = if (curtidos.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun mostrarErro(mensagem: String) {
        progress.visibility = View.GONE
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show()
    }
}