package com.example.sisi

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sisi.databinding.ActivityWritePostBinding
import com.example.sisi.recyclerView.CustomAdapter
import com.example.sisi.recyclerView.ImageData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.FileOutputStream
import java.text.SimpleDateFormat

private lateinit var addImageBtn :ImageButton
private lateinit var recycler : RecyclerView
private lateinit var user : FirebaseUser
private lateinit var userName :String
private lateinit var userUid : String

private val imageData:MutableList<ImageData> = mutableListOf()
class WritePost : AppCompatActivity() {
    val CAMERA_PERMISSION = arrayOf(Manifest.permission.CAMERA)
    val STORAGE_PERMISSION = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val FLAG_PERM_CAMERA = 98
    val FLAG_PERM_STORAGE =99

    val FLAG_REQ_CAMERA = 101
    val FLAG_REQ_GALLERY = 102
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_post)
        val binding = ActivityWritePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = Firebase.auth.currentUser!!
        userUid = user.uid

        recycler = binding.writeRecyclerView
        addImageBtn = binding.addImageButton

        //1데이터 로딩
        addImageBtn.setOnClickListener{
            val dialog = AlertDialog.Builder(this)
                .setCancelable(true)
                .setView(R.layout.dialog_cam)
                .create()
            dialog.findViewById<Button>(R.id.resetImage)?.setText("취소")
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
                dialog.dismiss()
            }
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
    fun saveImageFile(filename:String, minType :String,bitmap: Bitmap) : Uri?{
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
                    return uri
                }
            }
        }catch (e:Exception){
            Log.e("camera", "${e.localizedMessage}")
        }
        return null
    }

    //사진 파일명 생성
    fun newFileName(): String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmSS")
        val time = sdf.format(System.currentTimeMillis())
        return "${userUid}${time}.jpg"
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                FLAG_REQ_CAMERA ->{
                    if(data?.extras?.get("data") != null){
                        val bitmap = data?.extras?.get("data") as Bitmap

                        val filename = newFileName()
                        val uri = saveImageFile(filename,"image/jpg", bitmap)

                        imageData.add(ImageData(uri))
                        //2어댑터 생성
                        val adapter = CustomAdapter()
                        // 3어댑터에 데이터 전달
                        adapter.listData = imageData
                        //4 화면에 있는 리사이클러부에 아답터 연결
                        recycler.adapter = adapter
                        //5. 레이아웃 매니저 연결
                        recycler.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
                    }
                }
                FLAG_REQ_GALLERY ->{
                    val uri = data?.data
                    imageData.add(ImageData(uri))
                    //2어댑터 생성
                    val adapter = CustomAdapter()
                    // 3어댑터에 데이터 전달
                    adapter.listData = imageData
                    //4 화면에 있는 리사이클러부에 아답터 연결
                    recycler.adapter = adapter
                    //5. 레이아웃 매니저 연결
                    recycler.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
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

