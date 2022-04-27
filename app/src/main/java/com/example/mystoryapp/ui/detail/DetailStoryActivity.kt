package com.example.mystoryapp.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.mystoryapp.ListStoryItem
import com.example.mystoryapp.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {

    private var _detailStoryBinding: ActivityDetailStoryBinding? = null
    private val binding get() = _detailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _detailStoryBinding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val detailStory = intent.getParcelableExtra<ListStoryItem>(EXTRA_DETAIL) as ListStoryItem

        binding?.apply {
            btnBack.setOnClickListener { onBackPressed() }
            Glide.with(this@DetailStoryActivity)
                .load(detailStory.photoUrl)
                .into(imgDetailStories)
            tvDetailName.text = detailStory.name
            tvDetailCreated.text = detailStory.createdAt
            tvDetailDescription.text = detailStory.description
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _detailStoryBinding = null
    }

    companion object {
        const val EXTRA_DETAIL = "extra_detail"
    }

}