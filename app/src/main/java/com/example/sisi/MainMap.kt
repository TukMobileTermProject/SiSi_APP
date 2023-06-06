package com.example.sisi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sisi.databinding.ActivityMainMapBinding

class MainMap : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_writing -> {
                    true
                }
                R.id.nav_refresh -> {
                    finish() //인텐트 종료

                    overridePendingTransition(0, 0) //인텐트 효과 없애기

                    val intent = intent //인텐트

                    startActivity(intent) //액티비티 열기

                    overridePendingTransition(0, 0) //인텐트 효과 없애기

                    true
                }
                R.id.nav_mypage -> {
                    val intent = Intent(applicationContext, MyPageActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }


    }
}