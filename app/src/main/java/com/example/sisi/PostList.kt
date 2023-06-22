package com.example.sisi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sisi.databinding.ActivityPostBinding
import com.example.sisi.databinding.ActivityPostListBinding

class PostList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_list)
        val binding = ActivityPostListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.joinBackBtn2.setOnClickListener {
            val intent = Intent(this,MyPageActivity::class.java)
            startActivity(intent)
        }

        binding.imageView6.setOnClickListener {
            val intent = Intent(this, Post::class.java)
            startActivity(intent)
        }
    }
}