package br.unisanta.projetoddm.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.unisanta.projetoddm.R
import br.unisanta.projetoddm.data.SeedData
import br.unisanta.projetoddm.data.TmdbClient
import br.unisanta.projetoddm.data.model.MovieDoc
import br.unisanta.projetoddm.data.repository.AuthRepository
import br.unisanta.projetoddm.data.repository.MovieRepository
import br.unisanta.projetoddm.ui.BaseActivity
import br.unisanta.projetoddm.ui.auth.LoginActivity
import br.unisanta.projetoddm.ui.liked.LikedMoviesActivity
import br.unisanta.projetoddm.ui.movie.MovieDetailActivity
import br.unisanta.projetoddm.ui.recommendations.RecommendationsActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class HomeActivity : BaseActivity() {

    private val authRepository = AuthRepository()
    private val movieRepository = MovieRepository()

    private val todosOsFilmes = ArrayList<MovieDoc>()
    private var jaTentouPopular = false
    private var buscandoPosters = false

    private lateinit var adapter: MovieAdapter
    private lateinit var etSearch: TextInputEditText
    private lateinit var progress: ProgressBar
    private lateinit var tvEmpty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.inflateMenu(R.menu.menu_home)
        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_liked) {
                startActivity(Intent(this, LikedMoviesActivity::class.java))
                true
            } else if (item.itemId == R.id.action_logout) {
                sair()
                true
            } else {
                false
            }
        }
        authRepository.getUserName { nome ->
            toolbar.subtitle = "Olá, " + nome
        }

        etSearch = findViewById(R.id.etSearch)
        progress = findViewById(R.id.progress)
        tvEmpty = findViewById(R.id.tvEmpty)

        adapter = MovieAdapter { movie -> abrirDetalhe(movie) }
        val recycler = findViewById<RecyclerView>(R.id.rvMovies)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        etSearch.doAfterTextChanged { texto ->
            filtrar(texto.toString())
        }

        findViewById<MaterialButton>(R.id.btnRecommendations).setOnClickListener {
            startActivity(Intent(this, RecommendationsActivity::class.java))
        }

        carregarFilmes()
    }

    private fun carregarFilmes() {
        progress.visibility = View.VISIBLE
        movieRepository.getAllMovies(
            onSuccess = { movies ->
                if (movies.isEmpty() && !jaTentouPopular) {
                    popularCatalogo()
                } else {
                    progress.visibility = View.GONE
                    todosOsFilmes.clear()
                    todosOsFilmes.addAll(movies)
                    filtrar(etSearch.text.toString())
                    completarPosters()
                }
            },
            onError = { mensagem ->
                progress.visibility = View.GONE
                Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show()
            }
        )
    }

    // primeira execução: o Firestore está vazio, então grava o catálogo inicial
    private fun popularCatalogo() {
        jaTentouPopular = true
        movieRepository.seedMovies(SeedData.getMovies(),
            onSuccess = { carregarFilmes() },
            onError = { mensagem ->
                progress.visibility = View.GONE
                Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show()
            }
        )
    }

    // busca no TMDB o poster dos filmes que ainda não têm e salva no Firestore
    private fun completarPosters() {
        if (buscandoPosters || !TmdbClient.temChave()) return

        val semPoster = ArrayList<MovieDoc>()
        for (movie in todosOsFilmes) {
            if (movie.posterUrl.isBlank()) {
                semPoster.add(movie)
            }
        }
        if (semPoster.isEmpty()) return

        buscandoPosters = true

        Thread {
            val encontrados = HashMap<String, String>()
            for (movie in semPoster) {
                val url = TmdbClient.buscarPosterUrl(movie.title, movie.releaseYear)
                if (url.isNotEmpty()) {
                    movieRepository.updatePoster(movie.id, url)
                    encontrados[movie.id] = url
                }
            }

            runOnUiThread {
                buscandoPosters = false
                if (isFinishing || isDestroyed) return@runOnUiThread

                // aqui atualiza a lista na tela sem precisar buscar tudo de novo
                for (i in todosOsFilmes.indices) {
                    val url = encontrados[todosOsFilmes[i].id]
                    if (url != null) {
                        todosOsFilmes[i] = todosOsFilmes[i].copy(posterUrl = url)
                    }
                }
                filtrar(etSearch.text.toString())
            }
        }.start()
    }

    private fun filtrar(busca: String) {
        val termo = busca.trim().lowercase()
        val filtrados = ArrayList<MovieDoc>()

        for (movie in todosOsFilmes) {
            val bateTitulo = movie.title.lowercase().contains(termo)
            val bateDiretor = movie.director.lowercase().contains(termo)
            if (termo.isEmpty() || bateTitulo || bateDiretor) {
                filtrados.add(movie)
            }
        }

        adapter.submitList(filtrados)
        tvEmpty.visibility = if (filtrados.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun abrirDetalhe(movie: MovieDoc) {
        val intent = Intent(this, MovieDetailActivity::class.java)
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, movie.id)
        startActivity(intent)
    }

    private fun sair() {
        authRepository.logout()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}