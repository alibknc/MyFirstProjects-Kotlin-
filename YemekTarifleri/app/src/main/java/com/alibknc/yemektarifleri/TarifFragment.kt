package com.alibknc.yemektarifleri

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_tarif.*
import java.io.ByteArrayOutputStream
import java.lang.Exception

class TarifFragment : Fragment() {

    private var secilenGorsel: Uri? = null
    private var secilenBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tarif, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        kaydetButon.setOnClickListener {
            kaydet(it)
        }

        imageView.setOnClickListener {
            gorselSec(it)
        }

        arguments?.let {
            val bilgi = TarifFragmentArgs.fromBundle(it).bilgi

            if(bilgi == "menu"){
                isimText.setText("")
                malzemeText.setText("")
                kaydetButon.visibility = View.VISIBLE

                val placeholder = BitmapFactory.decodeResource(context?.resources, R.drawable.gorselsec)
                imageView.setImageBitmap(placeholder)
            }else{
                kaydetButon.visibility = View.INVISIBLE
                imageView.setOnClickListener(){}
                val id = TarifFragmentArgs.fromBundle(it).id
                context?.let {
                    try {
                        val db = it.openOrCreateDatabase("Yemekler", Context.MODE_PRIVATE, null)
                        val cursor = db.rawQuery("SELECT * FROM yemekler WHERE id = ?", arrayOf(id.toString()))

                        val yemekIsmiIndex = cursor.getColumnIndex("yemekismi")
                        val yemekMalzemeIndex = cursor.getColumnIndex("malzemeler")
                        val yemekGorselIndex = cursor.getColumnIndex("gorsel")

                        while (cursor.moveToNext()){
                            isimText.setText(cursor.getString(yemekIsmiIndex))
                            malzemeText.setText(cursor.getString(yemekMalzemeIndex))

                            val byteDizisi = cursor.getBlob(yemekGorselIndex)
                            val bitmap = BitmapFactory.decodeByteArray(byteDizisi, 0, byteDizisi.size)
                            imageView.setImageBitmap(bitmap)
                        }

                        cursor.close()
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun kaydet(view: View) {
        val yemekIsmi = isimText.text.toString()
        val malzemeler = malzemeText.text.toString()

        if(secilenBitmap != null){
            val yeniBitmap = gorselKucult(secilenBitmap!!, 300)
            val outputStream = ByteArrayOutputStream()
            yeniBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
            val byteDizisi = outputStream.toByteArray()

            try {
                context?.let {
                    val database = it.openOrCreateDatabase("Yemekler", Context.MODE_PRIVATE, null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS yemekler (id INTEGER PRIMARY KEY, yemekismi VARCHAR, malzemeler VARCHAR, gorsel BLOB)")
                    val sqlString = "INSERT INTO yemekler (yemekismi, malzemeler, gorsel) VALUES (?, ?, ?)"
                    val statement = database.compileStatement(sqlString)
                    statement.bindString(1,yemekIsmi)
                    statement.bindString(2,malzemeler)
                    statement.bindBlob(3,byteDizisi)
                    statement.execute()
                }
            }catch (e: Exception){
                e.printStackTrace()
            }

            val action = TarifFragmentDirections.actionTarifFragmentToListeFragment()
            Navigation.findNavController(view).navigate(action)
        }
    }

    private fun gorselSec(view: View) {
        activity?.let {
            if (ContextCompat.checkSelfPermission(
                    it.applicationContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            } else {
                val galeriIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent, 2)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val galeriIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent, 2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            secilenGorsel = data.data

            try {
                context?.let {
                    if (secilenGorsel != null) {
                        if(Build.VERSION.SDK_INT >= 28) {
                            val source = ImageDecoder.createSource(it.contentResolver, secilenGorsel!!)
                            secilenBitmap = ImageDecoder.decodeBitmap(source)
                            imageView.setImageBitmap(secilenBitmap)
                        }else{
                            secilenBitmap = MediaStore.Images.Media.getBitmap(it.contentResolver, secilenGorsel)
                            imageView.setImageBitmap(secilenBitmap)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun gorselKucult(bitmap: Bitmap, maxBoyut: Int) : Bitmap{
        var width = bitmap.width
        var height = bitmap.height

        var bitmapOrani : Double = width.toDouble() / height.toDouble()

        if(bitmapOrani > 1){
            width = maxBoyut
            val yeniHeight = width / bitmapOrani
            height = yeniHeight.toInt()
        }else{
            height = maxBoyut
            val yeniWidth = height * bitmapOrani
            width = yeniWidth.toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true)

    }

}