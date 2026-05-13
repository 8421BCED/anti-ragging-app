package com.joel.antirag

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.joel.antirag.databinding.ItemComplaintBinding

class ComplaintAdapter(private var complaints: List<Complaint>) : RecyclerView.Adapter<ComplaintAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemComplaintBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemComplaintBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val complaint = complaints[position]
        holder.binding.tvProblemSummary.text = complaint.problem
        holder.binding.tvStatus.text = complaint.status
        
        val color = when (complaint.status.lowercase()) {
            "approved" -> android.R.color.holo_green_light
            "declined" -> android.R.color.holo_red_light
            else -> android.R.color.holo_orange_light
        }
        holder.binding.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, color))
    }

    override fun getItemCount() = complaints.size

    fun updateData(newData: List<Complaint>) {
        complaints = newData
        notifyDataSetChanged()
    }
}
