package com.dicoding.asclepius.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val statText = intent.getStringExtra("label_text") ?: "No label available"
        val persentase = intent.getStringExtra("confidence_text") ?: "No confidence available"
        val imageUriString = intent.getStringExtra("image_uri") ?: ""

        if (imageUriString.isNotEmpty()) {
            val imageUri = Uri.parse(imageUriString)
            binding.resultImage.setImageURI(imageUri)
        }

        binding.statText.text = statText
        binding.persentase.text = persentase

    }
}