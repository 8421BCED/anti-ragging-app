package com.joel.antirag

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.joel.antirag.databinding.ActivityComplaintBinding
import com.google.android.gms.location.LocationServices
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.util.UUID



class ComplaintActivity : AppCompatActivity() {

    private lateinit var binding: ActivityComplaintBinding
    private var imageUri: Uri? = null
    private var isAnonymous = false

    private val departments = arrayOf("IT", "AIDS", "AIML", "MECH", "ECE", "CSE", "CIVIL", "CSBS")
    private val sections = arrayOf("A", "B", "C", "D", "E", "F", "G")
    private val years = arrayOf("1", "2", "3", "4")

    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data
            binding.ivPreview.visibility = View.VISIBLE
            binding.ivPreview.setImageURI(imageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComplaintBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDropdowns()

        isAnonymous = intent.getBooleanExtra("is_anonymous", false)
        if (isAnonymous) {
            setupAnonymousMode()
        }

        binding.btnUploadImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImage.launch(intent)
        }

        binding.btnSubmit.setOnClickListener {
            submitComplaint()
        }
    }

    private fun setupDropdowns() {
        val deptAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, departments)
        binding.spDept.setAdapter(deptAdapter)

        val secAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, sections)
        binding.spSection.setAdapter(secAdapter)

        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, years)
        binding.spYear.setAdapter(yearAdapter)
    }

    private fun setupAnonymousMode() {
        binding.tvTitle.text = "ANONYMOUS REPORT"
        binding.tilName.visibility = View.GONE
        binding.tilRollNo.visibility = View.GONE
        binding.layoutStudentInfo.visibility = View.GONE
        binding.tilDept.visibility = View.GONE
        
        binding.btnSubmit.backgroundTintList = androidx.core.content.ContextCompat.getColorStateList(this, R.color.sos_red)
        binding.btnSubmit.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.white))
    }

    private fun submitComplaint() {
        val problem = binding.etProblem.text.toString()
        if (problem.isEmpty()) {
            Toast.makeText(this, "Please describe the problem", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnSubmit.isEnabled = false
        binding.btnSubmit.text = "Submitting..."

        CoroutineScope(Dispatchers.IO).launch {
            try {
                var imageUrl: String? = null
                
                // 1. Upload image if exists
                imageUri?.let { uri ->
                    val bytes = contentResolver.openInputStream(uri)?.readBytes()
                    if (bytes != null) {
                        val fileName = "${UUID.randomUUID()}.jpg"
                        SupabaseManager.client.storage.from("complaints").upload(fileName, bytes)
                        imageUrl = SupabaseManager.client.storage.from("complaints").publicUrl(fileName)
                    }
                }

                // 2. Capture Location
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@ComplaintActivity)
                var lat: Double? = null
                var lng: Double? = null
                
                try {
                    val location = withContext(Dispatchers.IO) {
                        // Using a simple task-based approach for brevity
                        if (androidx.core.app.ActivityCompat.checkSelfPermission(this@ComplaintActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                            com.google.android.gms.tasks.Tasks.await(fusedLocationClient.lastLocation)
                        } else null
                    }
                    lat = location?.latitude
                    lng = location?.longitude
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                // 3. Insert into database
                val complaint = if (isAnonymous) {
                    Complaint(problem = problem, image_url = imageUrl, is_anonymous = true, latitude = lat, longitude = lng)
                } else {
                    Complaint(
                        name = binding.etName.text.toString(),
                        roll_no = binding.etRollNo.text.toString(),
                        year = binding.spYear.text.toString(),
                        section = binding.spSection.text.toString(),
                        department = binding.spDept.text.toString(),
                        problem = problem,
                        image_url = imageUrl,
                        is_anonymous = false,
                        latitude = lat,
                        longitude = lng
                    )
                }

                SupabaseManager.client.postgrest["complaints"].insert(complaint)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ComplaintActivity, "Complaint submitted successfully!", Toast.LENGTH_LONG).show()
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.btnSubmit.isEnabled = true
                    binding.btnSubmit.text = "SUBMIT COMPLAINT"
                    Toast.makeText(this@ComplaintActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
