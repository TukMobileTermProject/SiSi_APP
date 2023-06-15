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
import com.example.sisi.databinding.ActivityJoinBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text
import java.util.regex.Pattern

class JoinActivity : AppCompatActivity() {
    private lateinit var joinBtn :Button
    private lateinit var  nameInput :EditText
    private lateinit var mailInput :EditText
    private lateinit var pwdInput :EditText
    private lateinit var pwdcheckInput :EditText
    private lateinit var db :FirebaseFirestore
    private lateinit var auth :FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)
        val binding = ActivityJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        joinBtn = binding.joinJoinBtn
        nameInput = binding.joinInputNameEdt
        mailInput = binding.joinInputEmailEdt
        pwdInput = binding.joinInputPwEdt
        pwdcheckInput = binding.joinInputPwChkEdt
        db = FirebaseFirestore.getInstance()
        auth = Firebase.auth
        joinBtn.setOnClickListener {
            val mail = mailInput.text.toString()
            val name = nameInput.text.toString()
            val pwd = pwdInput.text.toString()
            val pwdCheck = pwdcheckInput.text.toString()
            val pattern :Pattern = android.util.Patterns.EMAIL_ADDRESS

            if(pattern.matcher(mail).matches() && pwd == pwdCheck){
                auth.createUserWithEmailAndPassword(mail, pwd)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val user = auth.currentUser
                            val layoutResId = R.layout.dialog_ok
                            val dialog = AlertDialog.Builder(this)
                                .setCancelable(false)
                                .setView(layoutResId)
                                .create()
                            dialog.show()
                            dialog.findViewById<TextView>(R.id.okDialogTextView)?.setText("회원가입 완료 되었습니다. \\n 로그인 해주세요!!")
                            dialog.findViewById<Button>(androidx.core.R.id.dialog_button)?.setOnClickListener {
                                dialog.dismiss()
                                var outIntent = Intent(this, LoginActivity::class.java)
                                setResult(Activity.RESULT_OK)
                            }
                            var userData:UserData = UserData()

                            userData.uid = user!!.uid
                            userData.email = user!!.email
                            userData.name = name
                            db?.collection("UserData")?.document(user?.uid.toString())?.set(userData)
                        } else {
                            // If sign in fails, display a message to the user.
                            val layoutResId = R.layout.dialog_ok
                            val dialog = AlertDialog.Builder(this)
                                .setCancelable(false)
                                .setView(layoutResId)
                                .create()
                            dialog.show()
                            dialog.findViewById<TextView>(R.id.okDialogTextView)?.setText("회원가입에 실패 하였습니다. \\n 이메일 비밀번호를 확인해 주세요.")
                            dialog.findViewById<Button>(R.id.okDialogBtn)?.setOnClickListener {
                                dialog.dismiss()
                                var outIntent = Intent(this, LoginActivity::class.java)
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        }
                    }
            }
            else{
                val layoutResId = R.layout.dialog_ok
                val dialog = AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setView(layoutResId)
                    .create()
                dialog.show()
                dialog.findViewById<TextView>(R.id.okDialogTextView)?.setText("회원가입에 실패 하였습니다 \\n 이메일 비밀번호를 확인해 주세요.")
                dialog.findViewById<Button>(R.id.okDialogBtn)?.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }
    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {

        }
    }
}