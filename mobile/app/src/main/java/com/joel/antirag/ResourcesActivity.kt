package com.joel.antirag

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.joel.antirag.databinding.ActivityResourcesBinding

class ResourcesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResourcesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResourcesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
    }
}
