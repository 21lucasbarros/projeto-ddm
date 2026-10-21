package br.unisanta.projetoddm.ui.movie

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.unisanta.projetoddm.R
import br.unisanta.projetoddm.data.model.MovieDoc
import br.unisanta.projetoddm.data.repository.AuthRepository
import br.unisanta.projetoddm.data.repository.MovieRepository
import br.unisanta.projetoddm.data.repository.RatingRepository
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class MovieDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MOVIE_ID = "movie_id"
    }

    private val authRepository = AuthRepository()
    private val movieRepository = MovieRepository()
    private val ratingRepository = RatingRepository()

    private var movieId = ""

    private lateinit var tvTitle: TextView
    private lateinit var tvInfo: TextView
    private lateinit var tvGenres: TextView
    private lateinit var tvFranchise: TextView
    private lateinit var tvCast: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var btnSave: MaterialButton
    private lateinit var progress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        tvTitle = findViewById(R.id.tvTitle)
        tvInfo = findViewById(R.id.tvInfo)
        tvGenres = findViewById(R.id.tvGenres)
        tvFranchise = findViewById(R.id.tvFranchise)
        tvCast = findViewById(R.id.tvCast)
        ratingBar = findViewById(R.id.ratingBar)
        btnSave = findViewById(R.id.btnSave)
        progress = findViewById(R.id.progress)

        val idRecebido = intent.getStringExtra(EXTRA_MOVIE_ID)
        if (idRecebido == null) {
            finish()
            return
        }
        movieId = idRecebido

        btnSave.setOnClickListener { salvarAvaliacao() }

        carregarFilme()
        carregarMinhaNota()
    }

    private fun carregarFilme() {
        progress.visibility = View.VISIBLE
        movieRepository.getMovie(movieId,
            onSuccess = { movie ->
                progress.visibility = View.GONE
                mostrarFilme(movie)
            },
            onError = { mensagem ->
                progress.visibility = View.GONE
                Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun mostrarFilme(movie: MovieDoc) {
        tvTitle.text = movie.title
        tvInfo.text = movie.releaseYear.toString() + " • Direção: " + movie.director + " • Nota " + movie.rating
        tvGenres.text = movie.genres.joinToString(" • ")

        val franquia = movie.franchise
        if (franquia == null) {
            tvFranchise.visibility = View.GONE
        } else {
            tvFranchise.visibility = View.VISIBLE
            tvFranchise.text = "Franquia: " + franquia
        }

        val elenco = StringBuilder()
        for (membro in movie.cast) {
            elenco.append("• ").append(membro.actorName)
                .append(" (").append(traduzirPapel(membro.role)).append(")\n")
        }
        tvCast.text = elenco.toString().trim()
    }

    private fun traduzirPapel(role: String): String {
        if (role == "PROTAGONIST") return "protagonista"
        if (role == "CAMEO") return "participação"
        return "coadjuvante"
    }

    private fun carregarMinhaNota() {
        val userId = authRepository.getCurrentUserId()
        ratingRepository.getUserRatingForMovie(userId, movieId) { nota ->
            ratingBar.rating = nota.toFloat()
        }
    }

    private fun salvarAvaliacao() {
        val nota = ratingBar.rating.toInt()
        if (nota == 0) {
            Toast.makeText(this, "Escolha de 1 a 5 estrelas.", Toast.LENGTH_SHORT).show()
            return
        }

        btnSave.isEnabled = false
        val userId = authRepository.getCurrentUserId()
        ratingRepository.saveRating(userId, movieId, nota,
            onSuccess = {
                btnSave.isEnabled = true
                val mensagem = if (nota >= 4) {
                    "Avaliação salva! Esse filme agora conta para suas recomendações."
                } else {
                    "Avaliação salva!"
                }
                Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
            },
            onError = { mensagem ->
                btnSave.isEnabled = true
                Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show()
            }
        )
    }
}