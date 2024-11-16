package com.dicoding.asclepius.view

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private var currentImageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = this
        )

        binding.galleryButton.setOnClickListener { startGallery() }

        binding.analyzeButton.setOnClickListener {
            currentImageUri?.let { analyzeImage(it) }
                ?: showToast(getString(R.string.empty_image_warning))
        }
    }


    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        when (uri) {
            null -> Log.d("Photo Picker", "Image selection canceled")
            else -> {
                currentImageUri = uri
                showImage(uri)
            }
        }
    }


    private fun showImage(uri: Uri) {
        binding.previewImageView.setImageURI(uri)
            .also { Log.d("Image Loading", "Loaded image: $uri") }

    }

    private fun analyzeImage(imageUri: Uri) {
        imageClassifierHelper.classifyStaticImage(imageUri)
        showToast("Analyzing image...")
//        Log.d("Image Analysis", "Analyzing image: $imageUri")
    }

    fun onResults(results: List<Classifications>?) {
        val statText = StringBuilder()
        val persentase = StringBuilder()

        if (results.isNullOrEmpty()) {
            statText.append("No results")
            persentase.append("No results")
        }

        results?.forEach { output ->
            val label = output.categories.firstOrNull()?.label ?: "Unknown"
            val confidence = output.categories.firstOrNull()?.score ?: 0.0f
            val confidencePercentage = (confidence * 100).toInt()


            statText.append("Status: $label")
            persentase.append("Persentase: $confidencePercentage%")
        }

        moveToResult(statText.toString(), persentase.toString())
    }
    fun onError(error: String) {
        showToast("Error: $error")
    }

    private fun moveToResult(statText: String, persentase: String) {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("label_text", statText)
            putExtra("confidence_text", persentase)
            putExtra("image_uri", currentImageUri.toString())
        }
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}