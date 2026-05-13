package com.joel.antirag

import android.Manifest
import android.content.Intent
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.*
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.joel.antirag.databinding.ActivityMainBinding
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.Serializable



class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val handler = Handler(Looper.getMainLooper())
    private var sosRunnable: Runnable? = null
    private val SOS_DELAY = 3000L // 3 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupSosButton()
        setupNavigation()
        setupRealtimeListener()
    }

    private fun setupRealtimeListener() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val channel = SupabaseManager.client.channel("complaints_changes")
                val flow = channel.postgresChangeFlow<io.github.jan.supabase.realtime.PostgresAction.Update>(schema = "public") {
                    table = "complaints"
                }
                
                flow.onEach { action ->
                    val status = action.record["status"]?.toString() ?: "updated"
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Complaint Status Update: $status", Toast.LENGTH_LONG).show()
                    }
                }.launchIn(this)

                channel.subscribe()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private var isSosCounting = false

    private fun setupSosButton() {
        binding.btnSos.setOnClickListener {
            if (isSosCounting) {
                cancelSosTimer()
            } else {
                startSosTimer()
            }
        }
    }

    private fun startSosTimer() {
        isSosCounting = true
        binding.btnSos.text = "X"
        Toast.makeText(this, "SOS triggering in 3 seconds. Tap X to cancel.", Toast.LENGTH_SHORT).show()
        sosRunnable = Runnable {
            triggerSos()
            resetSosButton()
        }
        handler.postDelayed(sosRunnable!!, SOS_DELAY)
    }

    private fun cancelSosTimer() {
        sosRunnable?.let { handler.removeCallbacks(it) }
        sosRunnable = null
        resetSosButton()
        Toast.makeText(this, "SOS Cancelled", Toast.LENGTH_SHORT).show()
    }

    private fun resetSosButton() {
        isSosCounting = false
        binding.btnSos.text = "SOS"
    }

    private fun triggerSos() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                triggerVibration()
                sendSosToSupabase(it.latitude, it.longitude)
            } ?: run {
                Toast.makeText(this, "Unable to get location. Please enable GPS.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun triggerVibration() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(200)
        }
    }

    private fun sendSosToSupabase(lat: Double, lng: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val alert = SosAlert(latitude = lat, longitude = lng)
                SupabaseManager.client.postgrest["sos_alerts"].insert(alert)
                launch(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "SOS Sent Successfully!", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Failed to send SOS: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupNavigation() {
        binding.cardRegister.setOnClickListener {
            startActivity(Intent(this, ComplaintActivity::class.java))
        }
        binding.cardAnonymous.setOnClickListener {
            val intent = Intent(this, ComplaintActivity::class.java)
            intent.putExtra("is_anonymous", true)
            startActivity(intent)
        }
        binding.cardTrack.setOnClickListener {
            startActivity(Intent(this, TrackActivity::class.java))
        }
        binding.btnNotifications.setOnClickListener {
            startActivity(Intent(this, TrackActivity::class.java)) // Redirect to Track to see status
        }
        binding.cardResources.setOnClickListener {
            startActivity(Intent(this, ResourcesActivity::class.java))
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_reports -> {
                    startActivity(Intent(this, TrackActivity::class.java))
                    true
                }
                R.id.nav_support -> {
                    startActivity(Intent(this, SupportActivity::class.java))
                    true
                }
                R.id.nav_data, R.id.nav_settings -> {
                    Toast.makeText(this, "${item.title} coming soon!", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }
}
