package com.example.dscheduler.ui.record

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dscheduler.R
import com.example.dscheduler.data.GenreStatsWithCount

class StatsAdapter : ListAdapter<GenreStatsWithCount, StatsAdapter.StatsViewHolder>(StatsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stats, parent, false)
        return StatsViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatsViewHolder, position: Int) {
        holder.bind(getItem(position), currentList)
    }

    class StatsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textGenre: TextView = itemView.findViewById(R.id.textGenre)
        private val textTime: TextView = itemView.findViewById(R.id.textTime)
        private val textCount: TextView = itemView.findViewById(R.id.textCount)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        private val textPercentage: TextView = itemView.findViewById(R.id.textPercentage)

        fun bind(stats: GenreStatsWithCount, allStats: List<GenreStatsWithCount>) {
            textGenre.text = stats.genre
            
            // 시간 포맷팅
            val hours = stats.totalTime / (1000 * 60 * 60)
            val minutes = (stats.totalTime % (1000 * 60 * 60)) / (1000 * 60)
            textTime.text = "${hours}시간 ${minutes}분"
            
            textCount.text = "${stats.count}회"
            
            // 전체 시간 대비 비율 계산 (전체 시간 대비 각 장르의 비율)
            if (allStats.isNotEmpty()) {
                val totalTime = allStats.sumOf { it.totalTime }
                val percentage = if (totalTime > 0) (stats.totalTime * 100 / totalTime) else 0
                progressBar.progress = percentage.toInt()
                textPercentage.text = "${percentage}%"
            }
        }
    }

    private class StatsDiffCallback : DiffUtil.ItemCallback<GenreStatsWithCount>() {
        override fun areItemsTheSame(oldItem: GenreStatsWithCount, newItem: GenreStatsWithCount): Boolean {
            return oldItem.genre == newItem.genre
        }

        override fun areContentsTheSame(oldItem: GenreStatsWithCount, newItem: GenreStatsWithCount): Boolean {
            return oldItem == newItem
        }
    }
} 