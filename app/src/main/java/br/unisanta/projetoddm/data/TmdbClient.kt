package br.unisanta.projetoddm.data

import br.unisanta.projetoddm.BuildConfig
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object TmdbClient {

    private const val SEARCH_URL = "https://api.themoviedb.org/3/search/movie"
    private const val IMAGE_URL = "https://image.tmdb.org/t/p/w500"

    fun temChave(): Boolean {
        return BuildConfig.TMDB_API_KEY.isNotBlank()
    }

    // tenta com o ano, se não achar, tenta só pelo título
    fun buscarPosterUrl(titulo: String, ano: Int): String {
        var url = buscar(titulo, ano)
        if (url.isEmpty()) {
            url = buscar(titulo, 0)
        }
        return url
    }

    private fun buscar(titulo: String, ano: Int): String {
        val query = URLEncoder.encode(titulo, "UTF-8")
        var endereco = SEARCH_URL + "?api_key=" + BuildConfig.TMDB_API_KEY +
                "&query=" + query + "&language=pt-BR"
        if (ano > 0) {
            endereco = endereco + "&year=" + ano
        }

        var conexao: HttpURLConnection? = null
        try {
            conexao = URL(endereco).openConnection() as HttpURLConnection
            conexao.connectTimeout = 10000
            conexao.readTimeout = 10000

            if (conexao.responseCode != 200) return ""

            val texto = conexao.inputStream.bufferedReader().use { it.readText() }
            val resultados = JSONObject(texto).getJSONArray("results")
            if (resultados.length() == 0) return ""

            val posterPath = resultados.getJSONObject(0).optString("poster_path", "")
            if (posterPath.isEmpty() || posterPath == "null") return ""

            return IMAGE_URL + posterPath
        } catch (e: Exception) {
            return ""
        } finally {
            conexao?.disconnect()
        }
    }
}