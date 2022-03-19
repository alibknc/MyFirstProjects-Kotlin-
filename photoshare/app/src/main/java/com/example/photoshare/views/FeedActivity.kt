package com.example.photoshare.views

import adapters.FeedRecyclerAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoshare.NewPostActivity
import com.example.photoshare.R
import com.example.photoshare.models.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_feed.*

class FeedActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private lateinit var recyclerViewAdapter: FeedRecyclerAdapter
    var postList = ArrayList<Post>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        verileriAl()

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerViewAdapter = FeedRecyclerAdapter(postList)
        recyclerView.adapter = recyclerViewAdapter
    }

    fun verileriAl(){
        database.collection("Post").orderBy("time", Query.Direction.DESCENDING).addSnapshotListener{ snapshot, exception ->
            if(exception != null){
                Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG).show()
            }else{
                if(snapshot != null){
                    if(!snapshot.isEmpty){
                        val documents = snapshot.documents

                        postList.clear()
                        for (document in documents){
                            var alinanPost = Post(document.get("email").toString(), document.get("yorum").toString(), document.get("url").toString())
                            postList.add(alinanPost)
                        }
                    }
                    recyclerViewAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.home_settings, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.share){
            val intent = Intent(this, NewPostActivity::class.java)
            startActivity(intent)
        }else if(item.itemId == R.id.signOut){
            auth.signOut()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}