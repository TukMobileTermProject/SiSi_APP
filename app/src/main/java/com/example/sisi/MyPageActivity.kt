package com.example.sisi

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.sisi.databinding.ActivityMypageBinding
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Binder
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private lateinit var profileImage  : ImageView
private lateinit var nameText : TextView
private lateinit var modifyPwBtn  : Button
private lateinit var modifyProfileImageBtn  : Button
private lateinit var findListBtn : Button
private lateinit var user :FirebaseUser
private lateinit var userName :String
private lateinit var userEmail :String
private lateinit var userUid : String
private lateinit var db :FirebaseFirestore

class MyPageActivity : AppCompatActivity() {
    val CAMERA_PERMISSION = arrayOf(Manifest.permission.CAMERA)
    val STORAGE_PERMISSION = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val FLAG_PERM_CAMERA = 98
    val FLAG_PERM_STORAGE =99
    val FLAG_REQ_CAMERA = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivityMypageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        profileImage = binding.mypageProfileImage
        nameText = binding.mypageUserName
        modifyPwBtn = binding.mypageModifyPw
        findListBtn = binding.mypageFindList
        user = Firebase.auth.currentUser!!
        userUid = user.uid
        userEmail = user.email.toString()
        db = Firebase.firestore


        //비밀번호 재설정 버튼
        modifyPwBtn.setOnClickListener {
            Firebase.auth.sendPasswordResetEmail(userEmail)
                .addOnCompleteListener { task ->
                    val layoutResId = R.layout.dialog_ok
                    val dialog = AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setView(layoutResId)
                        .create()
                    dialog.show()
                    dialog.findViewById<TextView>(R.id.okDialogTextView)?.setText("비밀번호 재설정 이메일을 발송 했습니다.\n 이메일을 확인해 주세요.")
                    dialog.findViewById<Button>(R.id.okDialogBtn)?.setOnClickListener {
                        dialog.dismiss()
                    }
                }
        }
        //프로필 사진 변경

        profileImage.setOnClickListener {
            val layoutResId = R.layout.dialog_cam
            val dialog = AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(layoutResId)
                .create()
            dialog.show()
            dialog.findViewById<Button>(R.id.camera)?.setOnClickListener {
                if(isPermitted(CAMERA_PERMISSION)){
                    openCamera()
                    dialog.dismiss()
                }else{
                    ActivityCompat.requestPermissions(this, CAMERA_PERMISSION, FLAG_PERM_CAMERA )
                }
            }

            dialog.findViewById<Button>(R.id.gallery)?.setOnClickListener {

            }
            dialog.findViewById<Button>(R.id.resetImage)?.setOnClickListener {

            }
        }

        //이름 db에서 받아오는 코드
        val docRef = db.collection("UserData").document(userUid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    userName = document["name"].toString()
                    nameText.setText(userName)
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
        //프로필 사진 관련 코드
        val storage = Firebase.storage("gs://sisi-6e562.appspot.com/")
        var  storageRef = storage.reference
    }
    fun isPermitted(permissions: Array<String>) : Boolean{
        for(permission in permissions){
            val result = ContextCompat.checkSelfPermission(this,permission)
            if(result != PackageManager.PERMISSION_GRANTED){
                return false
            }
        }

        return true
    }
    fun openCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent , FLAG_REQ_CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                FLAG_REQ_CAMERA ->{
                    val bitmap = data?.extras?.get("data") as Bitmap
                    profileImage.setImageBitmap(bitmap)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            FLAG_PERM_CAMERA ->{
                var checked =true
                for(grant in grantResults){
                    if(grant != PackageManager.PERMISSION_GRANTED){
                        checked = false
                        break
                    }
                }
                if(checked){
                    openCamera()
                }
            }
        }
    }
}