package br.unisanta.projetoddm.ui.home

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.unisanta.projetoddm.R
import br.unisanta.projetoddm.data.model.MovieDoc
import com.bumptech.glide.Glide

class MovieAdapter(
    private val onClick: (MovieDoc) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private val items = ArrayList<MovieDoc>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(novos: List<MovieDoc>) {
        items.clear()
        items.addAll(novos)
        notifyDataSetChanged()
    }

    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPoster: ImageView = view.findViewById(R.id.ivPoster)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvDirector: TextView = view.findViewById(R.id.tvDirector)
        val tvGenres: TextView = view.findViewById(R.id.tvGenres)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = items[position]
        holder.tvTitle.text = movie.title + " (" + movie.releaseYear + ")"
        holder.tvDirector.text = "Direção: " + movie.director
        holder.tvGenres.text = movie.genres.joinToString(" • ")

        // fundo cinza basico enquanto carrega ou se o filme n tiver poster
        Glide.with(holder.ivPoster)
            .load(movie.posterUrl)
            .placeholder(ColorDrawable(Color.LTGRAY))
            .error(ColorDrawable(Color.LTGRAY))
            .into(holder.ivPoster)

        holder.itemView.setOnClickListener { onClick(movie) }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}