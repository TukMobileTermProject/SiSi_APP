package com.example.sisi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast

class MainMap : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_map)

        val png2 = findViewById<ImageButton>(R.id.toolbar)
        png2.setOnClickListener {
            val popup = android.widget.PopupMenu(applicationContext, it)

            menuInflater.inflate(R.menu.popup, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.mypage -> {
                        Toast.makeText(this, "마이페이지 클릭", Toast.LENGTH_SHORT).show()
                        val intent = Intent(applicationContext, MyPageActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    R.id.refresh -> {
                        Toast.makeText(this, "새로고침 클릭", Toast.LENGTH_SHORT).show()
                        true
                    }

                    else -> false
                }
            }

            popup.show()
        }


    }
}