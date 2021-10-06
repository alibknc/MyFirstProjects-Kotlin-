package com.alibknc.yemektarifleri

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_liste.*
import java.lang.Exception

class ListeFragment : Fragment() {

    var yemekIsimListesi = ArrayList<String>()
    var malzemeListesi = ArrayList<String>()
    var yemekIdListesi = ArrayList<Int>()
    private lateinit var listeAdapter : ListeRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_liste, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listeAdapter = ListeRecyclerAdapter(yemekIsimListesi, malzemeListesi, yemekIdListesi)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = listeAdapter
        sqlVeriAlma()
    }

    fun sqlVeriAlma() {
        try {
            activity?.let {
                val database = it.openOrCreateDatabase("Yemekler", Context.MODE_PRIVATE, null)

                val cursor = database.rawQuery("SELECT * FROM yemekler", null)
                val yemekIsmiIndex = cursor.getColumnIndex("yemekismi")
                val yemekIdIndex = cursor.getColumnIndex("id")
                val malzemeIndex = cursor.getColumnIndex("malzemeler")

                yemekIdListesi.clear()
                yemekIsimListesi.clear()
                malzemeListesi.clear()

                while (cursor.moveToNext()) {
                    yemekIsimListesi.add(cursor.getString(yemekIsmiIndex))
                    yemekIdListesi.add(cursor.getInt(yemekIdIndex))
                    malzemeListesi.add(cursor.getString(malzemeIndex))
                }

                listeAdapter.notifyDataSetChanged()

                cursor.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}