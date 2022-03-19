package com.example.photoshare

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_new_post.*
import java.util.*

class NewPostActivity : AppCompatActivity() {
    var gorsel : Uri? = null
    var bitmap : Bitmap? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseFirestore

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            gorsel = result.data!!.data
            val imageView: ImageView = findViewById(R.id.imageView)

            if(gorsel != null){
                if(Build.VERSION.SDK_INT >= 28){
                    val source = ImageDecoder.createSource(this.contentResolver, gorsel!!)
                    bitmap = ImageDecoder.decodeBitmap(source)
                    imageView.setImageBitmap(bitmap)
                }else{
                    bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, gorsel)
                    imageView.setImageBitmap(bitmap)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        database = FirebaseFirestore.getInstance()
    }

    fun paylas(view: View){
        val uuid = UUID.randomUUID()
        val isim = "$uuid.jpg"

        val reference = storage.reference
        var gorselReference = reference.child("images").child(isim)

        if(gorsel != null){
            gorselReference.putFile(gorsel!!).addOnSuccessListener {
                gorselReference = reference.child("images").child(isim)
                gorselReference.downloadUrl.addOnSuccessListener { uri ->
                    var url = uri.toString()
                    val email = auth.currentUser!!.email.toString()
                    val yorum = yorumText.text.toString()
                    val time = Timestamp.now()

                    val map = hashMapOf<String, Any>()
                    map.put("url", url)
                    map.put("email", email)
                    map.put("yorum", yorum)
                    map.put("time", time)

                    database.collection("Post").add(map).addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            finish()
                        }
                    }.addOnFailureListener { ex ->
                        Toast.makeText(applicationContext, ex.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener { ex ->
                    Toast.makeText(applicationContext, ex.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun gorselSec(view: View){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }else{
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            resultLauncher.launch(galleryIntent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(grantResults.size > 0){
            if(requestCode == 1){
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    resultLauncher.launch(galleryIntent)
                }
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}