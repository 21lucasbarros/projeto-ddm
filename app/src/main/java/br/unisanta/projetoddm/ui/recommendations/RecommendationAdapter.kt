package br.unisanta.projetoddm.ui.recommendations

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
import br.unisanta.projetoddm.data.recommendation.RecommendationItem
import com.bumptech.glide.Glide

class RecommendationAdapter(
    private val onClick: (RecommendationItem) -> Unit
) : RecyclerView.Adapter<RecommendationAdapter.ViewHolder>() {

    private val items = ArrayList<RecommendationItem>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(novos: List<RecommendationItem>) {
        items.clear()
        items.addAll(novos)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPoster: ImageView = view.findViewById(R.id.ivPoster)
        val tvRank: TextView = view.findViewById(R.id.tvRank)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvScore: TextView = view.findViewById(R.id.tvScore)
        val tvReasons: TextView = view.findViewById(R.id.tvReasons)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recommendation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.tvRank.text = "#" + (position + 1)
        holder.tvTitle.text = item.movie.title + " (" + item.movie.releaseYear + ")"
        holder.tvScore.text = "Compatibilidade: " + item.score

        // os motivos vem prontos do motor de grafo
        val motivos = StringBuilder("Por que recomendamos:\n")
        for (motivo in item.reasons) {
            motivos.append("• ").append(motivo).append("\n")
        }
        holder.tvReasons.text = motivos.toString().trim()

        // fundo cinza enquanto carrega ou se o filme não tiver poster
        Glide.with(holder.ivPoster)
            .load(item.movie.posterUrl)
            .placeholder(ColorDrawable(Color.LTGRAY))
            .error(ColorDrawable(Color.LTGRAY))
            .into(holder.ivPoster)

        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}