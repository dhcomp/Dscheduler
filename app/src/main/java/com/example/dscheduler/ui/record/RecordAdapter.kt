package com.example.dscheduler.ui.record

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dscheduler.R
import com.example.dscheduler.data.model.ActivityRecord
import java.text.SimpleDateFormat
import java.util.*

class RecordAdapter : RecyclerView.Adapter<RecordAdapter.RecordViewHolder>() {

    private val items = mutableListOf<ActivityRecord>()
    private val format = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun submitList(newList: List<ActivityRecord>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = "${item.title} (${item.genre})"
        holder.time.text = "${format.format(Date(item.startTime))} - ${format.format(Date(item.endTime))}"
    }

    override fun getItemCount(): Int = items.size

    class RecordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.textTitle)
        val time: TextView = view.findViewById(R.id.textTime)
    }
}
