package com.example.sisi

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
                        val layoutResId = R.layout.dialog_ok
                        val dialog = AlertDialog.Builder(this)
                            .setCancelable(false)
                            .setView(layoutResId)
                            .create()
                        dialog.show()
                        dialog.findViewById<TextView>(R.id.okDialogTextView)?.setText("비밀번호 재설정 이메일을 발송 했습니다.\n 이메일을 확인해 주세요.")
                        dialog.findViewById<Button>(R.id.okDialogBtn)?.setOnClickListener {
                            dialog.dismiss()
                            var outIntent = Intent(this, LoginActivity::class.java)
                            setResult(Activity.RESULT_OK)
                            finish()
                        }

                    }
                    else{
                        Toast.makeText(this,"실패"
                            ,Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}