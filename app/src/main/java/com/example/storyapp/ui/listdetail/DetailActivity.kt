package com.example.storyapp.ui.listdetail

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityDetailBinding
import com.example.storyapp.nonui.model.ListModel

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    companion object {
        const val STORY_DETAIL_EXTRA = "story_detail"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
    }

    private fun setupView(){
        val story = intent.getParcelableExtra<ListModel>(STORY_DETAIL_EXTRA) as ListModel

        Glide.with(applicationContext).load(story.photoUrl).into(binding.imageList)
        binding.tvName.text = story.name
        binding.tvDescription.text = story.description
    }
}