package com.example.sisi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.sisi.databinding.ActivityFindpwdBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class FindpwdActivity : AppCompatActivity() {
    lateinit var emailEdit:EditText
    lateinit var findBtn :Button
    lateinit var user : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_findpwd)
        val binding = ActivityFindpwdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        emailEdit = binding.inputemail
        findBtn = binding.FindBtn

        findBtn.setOnClickListener{
            val emailAddress = emailEdit.text.toString()

            Firebase.auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener{task ->
                    if(task.isSuccessful){
                       Toast.makeText(this,"성공"
                           ,Toast.LENGTH_LONG).show()
                    }
                    else{
                        Toast.makeText(this,"실패"
                            ,Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}