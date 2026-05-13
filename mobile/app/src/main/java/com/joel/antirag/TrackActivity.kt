package com.joel.antirag

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.joel.antirag.databinding.ActivityTrackBinding
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrackBinding
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = HistoryAdapter(emptyList())
        binding.rvComplaints.adapter = adapter

        fetchHistory()
    }

    private fun fetchHistory() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 1. Fetch Complaints
                val complaints = SupabaseManager.client.postgrest["complaints"]
                    .select()
                    .decodeList<Complaint>()

                // 2. Fetch SOS Alerts
                val sosAlerts = SupabaseManager.client.postgrest["sos_alerts"]
                    .select()
                    .decodeList<SosAlert>()

                // 3. Merge into HistoryItems
                val history = mutableListOf<HistoryItem>()
                
                complaints.forEach {
                    history.add(HistoryItem(
                        type = "COMPLAINT",
                        title = it.problem,
                        subtitle = if (it.is_anonymous) "Anonymous" else it.name ?: "Unknown",
                        status = it.status,
                        timestamp = it.created_at
                    ))
                }

                sosAlerts.forEach {
                    history.add(HistoryItem(
                        type = "SOS",
                        title = "Emergency SOS Alert",
                        subtitle = "Location: ${it.latitude}, ${it.longitude}",
                        status = "SENT",
                        timestamp = it.created_at
                    ))
                }

                // Sort by timestamp (newest first)
                val sortedHistory = history.sortedByDescending { it.timestamp }

                withContext(Dispatchers.Main) {
                    adapter.updateData(sortedHistory)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@TrackActivity, "Error fetching history: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
