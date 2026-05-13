package com.joel.antirag

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.joel.antirag.databinding.ActivitySupportBinding

class SupportActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySupportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        binding.btnCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:+918220171043")
            startActivity(intent)
        }

        binding.btnEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:joel.ug.23.cs@francisxavier.ac.in")
            intent.putExtra(Intent.EXTRA_SUBJECT, "FX AntiRag Support Request")
            startActivity(intent)
        }
    }
}
