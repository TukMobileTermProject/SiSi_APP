package com.example.sisi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.sisi.databinding.ActivityMypageBinding

private lateinit var profileImage  : ImageView
private lateinit var nameText : TextView
private lateinit var modifyPwBtn  : Button
private lateinit var modifyProfileImageBtn  : Button
private lateinit var findListBtn : Button

class MyPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivityMypageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        profileImage = binding.mypageProfileImage
        nameText = binding.mypageUserName
        modifyPwBtn = binding.mypageModifyPw
        modifyProfileImageBtn = binding.mypageModifyImage
        findListBtn = binding.mypageFindList
    }
}