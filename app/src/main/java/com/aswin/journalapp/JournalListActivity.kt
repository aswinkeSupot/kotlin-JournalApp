package com.aswin.journalapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.aswin.journalapp.databinding.ActivityJournalListBinding
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference

class JournalListActivity : AppCompatActivity() {

    lateinit var binding : ActivityJournalListBinding

    //Firebaser Reference
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var user: FirebaseUser
    var db = FirebaseFirestore.getInstance()
    lateinit var storageReference: StorageReference
    var collectionReference: CollectionReference = db.collection("Journal")

    lateinit var journalList: MutableList<Journal>
    lateinit var adapter: JournalRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_journal_list)

        // Set up Toolbar as the ActionBar
        setSupportActionBar(binding.toolbar)

        //Firebase Auth
        firebaseAuth = Firebase.auth
        user = firebaseAuth.currentUser!!

        //RecyclerView
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        //Post arrayList
        journalList = arrayListOf<Journal>()

    }

    // Getting all posts
    override fun onStart() {
        super.onStart()

        Log.i("TAGGY", "onStart JournalListActivity")

        collectionReference.whereEqualTo("userId",user.uid).get().addOnSuccessListener {
            Log.i("TAGGY", "SIZE : ${it.size()}")
            if (!it.isEmpty){
//                it.forEach {
//                    //convert snapshots to journal objects
//                    var journal = it.toObject(Journal::class.java)
//
//                    journalList.add(journal)
//                }

                for (document in it) {
                    var journal = Journal(
                        document.data["title"].toString(),
                        document.data["thoughts"].toString(),
                        document.data.get("imageUrl").toString(),
                        document.data.get("userId").toString(),
                        document.data.get("timeAdded") as Timestamp,
                        document.data.get("username").toString()
                    )
                    journalList.add(journal)
                }

                // RecyclerView
                adapter = JournalRecyclerAdapter(this,journalList)
                binding.recyclerView.setAdapter(adapter)
                adapter.notifyDataSetChanged()
            }else {
                binding.tvNoPost.visibility = View.VISIBLE
            }
        }.addOnFailureListener {
            Toast.makeText(this,"Something went wrong. Error:-" +it.localizedMessage.toString(),Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add -> if (user != null && firebaseAuth != null){
                val intent = Intent(this,AddJournalActivity::class.java)
                startActivity(intent)
            }
            R.id.action_signout -> {
                if (user != null && firebaseAuth != null){
                    firebaseAuth.signOut()
                    val intent = Intent(this,LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}