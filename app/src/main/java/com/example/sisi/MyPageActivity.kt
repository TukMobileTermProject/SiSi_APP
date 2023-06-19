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
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat

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
    val FLAG_REQ_GALLERY = 102
    val storage = Firebase.storage("gs://sisi-6e562.appspot.com/")
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

        downloadFromCloud(userUid)
        binding.mypageFindList.setOnClickListener {
            val intent = Intent(this,PostList::class.java)
            startActivity(intent)
        }
        //비밀번호 재설정 버튼
        modifyPwBtn.setOnClickListener {
            Firebase.auth.sendPasswordResetEmail(userEmail)
                .addOnCompleteListener { task ->
                    val layoutResId = R.layout.dialog_ok
                    val dialog = AlertDialog.Builder(this)
                        .setCancelable(true)
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
                .setCancelable(true)
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
                if(isPermitted(STORAGE_PERMISSION)){
                    openGallery()
                    dialog.dismiss()
                }else{
                    ActivityCompat.requestPermissions(this, STORAGE_PERMISSION, FLAG_PERM_STORAGE )
                }
            }
            dialog.findViewById<Button>(R.id.resetImage)?.setOnClickListener {
                profileImage.setImageResource(R.drawable.profile)
                dialog.dismiss()
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
    //사진 파일 저장
    fun saveImageFile(filename:String, minType :String,bitmap: Bitmap) :Uri?{
        var values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME,filename)
        values.put(MediaStore.Images.Media.MIME_TYPE, minType)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            values.put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        try {
            if(uri != null){
                var descripter = contentResolver.openFileDescriptor(uri,"w")
                if(descripter != null){
                    val fos = FileOutputStream(descripter.fileDescriptor)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    fos.close()
                    uploadToCloud(uri)
                    return uri
                }
            }
        }catch (e:Exception){
            Log.e("camera", "${e.localizedMessage}")
        }
        return null
    }
    //사진 파일명 생성
    fun newFileName():String{
        val filename = userName + ".jpg"
        return filename
    }
    fun openCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent , FLAG_REQ_CAMERA)
    }
    fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,FLAG_REQ_GALLERY)
    }
    //cloud에 사진 저장
   fun uploadToCloud(uri: Uri?){

        //파일 이름 생성.
        var fileName = "${userUid}.jpg"
        //파일 업로드, 다운로드, 삭제, 메타데이터 가져오기 또는 업데이트를 하기 위해 참조를 생성.
        //참조는 클라우드 파일을 가리키는 포인터라고 할 수 있음.
        var imagesRef = storage!!.reference.child("ProfileImage/").child(fileName)    //기본 참조 위치/images/${fileName}
        //이미지 파일 업로드
        imagesRef.putFile(uri!!).addOnSuccessListener {

        }.addOnFailureListener {

        }


    }
    //클라우드에서 이미지 내려 받기
    fun downloadFromCloud(uid:String){
        var fileName = "ProfileImage/${uid}.jpg"
        val storageRef = storage.getReference(fileName)

        storageRef.downloadUrl.addOnSuccessListener { uri->
            Glide.with(this)
                .load(uri)
                .into(profileImage)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                FLAG_REQ_CAMERA ->{
                    if(data?.extras?.get("data") != null){
                        val bitmap = data?.extras?.get("data") as Bitmap

                        val filename = newFileName()
                        val uri = saveImageFile(filename,"image/jpg", bitmap)

                        profileImage.setImageURI(uri)
                    }
                }
                FLAG_REQ_GALLERY ->{
                    val uri = data?.data
                    profileImage.setImageURI(uri)
                    uploadToCloud(uri)
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