package com.example.sisi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sisi.databinding.ActivityPostBinding

class Post : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        val binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.likeBtn.setOnClickListener {
            binding.likeText.setText("공감수 : 1")
        }
        binding.repleBtn.setOnClickListener {
            var text = binding.repleEdittext.getText().toString()
            binding.reple.setText(text)
        }
        binding.joinBackBtn3.setOnClickListener{
            val intent = Intent(this,MainMap::class.java)
            startActivity(intent)
        }
    }
}