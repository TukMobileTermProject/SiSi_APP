package com.example.sisi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

import androidx.appcompat.app.AppCompatActivity
import com.example.sisi.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

private lateinit var login_inputIdEdt : EditText
private lateinit var login_inputPWEdt : EditText
private lateinit var  login_joinBtn :Button
private lateinit var login_findPwBtn :Button
private lateinit var login_loginBtn :Button
private lateinit var login_google: ImageButton

class LoginActivity:AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001
    private lateinit var mauth :FirebaseAuth
    private var fbFireStore : FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = Firebase.auth
        mauth = FirebaseAuth.getInstance()
        var currentUser = auth?.currentUser
        fbFireStore = FirebaseFirestore.getInstance()
        // 자동 로그인 개발시는 OFF
       """ if (currentUser == null) {

            Timer().schedule(object : TimerTask() {
                override fun run() {
                    val intent: Intent = Intent(applicationContext, LoginActivity::class.java)
                    firebaseAuthSignOut()
                    startActivity(intent)
                    finish()
                }
            }, 2000)

        }else{

            Timer().schedule(object : TimerTask() {
                override fun run() {
                    val intent: Intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }, 2000)

        }"""
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        login_loginBtn = binding.loginLoginBtn
        login_joinBtn = binding.loginJoinBtn
        login_inputIdEdt = binding.loginInputIdEdt
        login_inputPWEdt = binding.loginInputPwEdt
        login_google = binding.loginGoogle
        login_findPwBtn = binding.loginFindPwBtn
        // Firebase 인증 객체 초기화
        auth = Firebase.auth
        // Google 로그인 구성
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        login_google.setOnClickListener {
            googleSignInClient = GoogleSignIn.getClient(this, gso)
            val signINIntent = googleSignInClient!!.signInIntent
            startActivityForResult(signINIntent, RC_SIGN_IN)
        }
        login_findPwBtn.setOnClickListener {
            val intent = Intent(this,FindpwdActivity::class.java)
            startActivityForResult(intent,0)
        }
        login_joinBtn.setOnClickListener {
            val intent = Intent(this,JoinActivity::class.java)
            startActivityForResult(intent,0)
        }
        login_loginBtn.setOnClickListener {
            signIn(login_inputIdEdt.text.toString(), login_inputPWEdt.text.toString())
        }
    }

    private fun signIn(email: String, password: String) {

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth?.signInWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent:Intent = Intent(this,MainMap::class.java)
                        var loginUser = auth.currentUser
                        startActivity(intent)
                    } else {
                        val layoutResId = R.layout.dialog_ok
                        val dialog = AlertDialog.Builder(this)
                            .setCancelable(false)
                            .setView(layoutResId)
                            .create()
                        dialog.show()
                        dialog.findViewById<TextView>(R.id.okDialogTextView)?.setText("로그인할 수 없습니다. \\n 이메일 비밀번호를 확인해 주세요")
                        dialog.findViewById<Button>(R.id.okDialogBtn)?.setOnClickListener{
                            dialog.dismiss()
                        }
                    }
                }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        firebaseAuthSignOut()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            }catch (e: ApiException) {
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken:String){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this){task ->
                val user = auth.currentUser
                user?.let {
                    val name = user.displayName
                    val email = user.email
                    val displayName = user.displayName
                    val photoUrl = user.photoUrl
                    val emailVerified = user.isEmailVerified

                }
            }
        val user = auth.currentUser
        var userData:UserData = UserData()

        userData.uid = user!!.uid
        userData.email = user!!.email
        userData.name = user!!.displayName
        fbFireStore?.collection("UserData")?.document(user?.uid.toString())?.set(userData)
        val intent = Intent(this, MainMap::class.java)
        startActivity(intent)
    }

    private fun firebaseAuthSignOut(){
        Firebase.auth.signOut()
    }
}