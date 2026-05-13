package com.joel.antirag

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.joel.antirag.databinding.ItemComplaintBinding

class HistoryAdapter(private var items: List<HistoryItem>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemComplaintBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemComplaintBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvProblemSummary.text = item.title
        holder.binding.tvStatus.text = item.status
        
        // You could add a type icon here if you want
        
        val color = when (item.status.lowercase()) {
            "approved" -> android.R.color.holo_green_light
            "declined" -> android.R.color.holo_red_light
            "sent" -> android.R.color.holo_blue_light
            else -> android.R.color.holo_orange_light
        }
        holder.binding.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, color))
    }

    override fun getItemCount() = items.size

    fun updateData(newData: List<HistoryItem>) {
        items = newData
        notifyDataSetChanged()
    }
}
