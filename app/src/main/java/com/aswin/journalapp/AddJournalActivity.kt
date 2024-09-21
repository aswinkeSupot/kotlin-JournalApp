package com.aswin.journalapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.aswin.journalapp.databinding.ActivityAddJournalBinding
import com.aswin.journalapp.singleton.JournalUser
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.Date

class AddJournalActivity : AppCompatActivity() {

    lateinit var binding : ActivityAddJournalBinding

    // Credentials
    var currentUserId: String = ""
    var currentUserName: String = ""

    // Firebase
    lateinit var auth: FirebaseAuth
    lateinit var user: FirebaseUser

    // Firebase Firestore
    var db : FirebaseFirestore = FirebaseFirestore.getInstance()
    lateinit var storageReference: StorageReference

    var collectionReference: CollectionReference = db.collection("Journal")
    lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_journal)

        storageReference = FirebaseStorage.getInstance().getReference()
        auth = Firebase.auth

        binding.apply {
            postProgressBar.visibility = View.INVISIBLE

            if(JournalUser.instance != null) {
                currentUserId = auth.currentUser?.uid.toString()
                //currentUserName = auth.currentUser?.displayName.toString()
                currentUserName = auth.currentUser?.email.toString()


                postUsernameTextview.text = currentUserName
            }

            // Getting image form gallery
            postCameraButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "image/*"
                }
                startActivityForResult(intent, 1)

//                var i : Intent = Intent(Intent.ACTION_GET_CONTENT)
//                i.setType("image/*")
//                startActivityForResult(i,1)
            }

            postSaveJournalButton.setOnClickListener {
                Savejournal()
            }

        }
    }

    private fun Savejournal() {
        var title: String = binding.postTitleEt.text.toString().trim()
        var thoughts: String = binding.postDescriptionEt.text.toString().trim()

        binding.postProgressBar.visibility = View.VISIBLE

        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(thoughts) && imageUri != null) {
            // Saving the path of images in Storage
            //  ...../journal_images/our_image.png    // Import TimeStamp (com.google.firebase.Timestamp) only
            val filePath: StorageReference = storageReference.child("journal_images").child("my_image_"+Timestamp.now().seconds)

            // Uploading the images
            filePath.putFile(imageUri).addOnSuccessListener {
                filePath.downloadUrl.addOnSuccessListener {
                    var imageUri: String = it.toString()
                    var timestamp: Timestamp = Timestamp(Date())

                    // Creating the object of Journal
                    var journal : Journal = Journal(title, thoughts, imageUri, currentUserId, timestamp, currentUserName)

                    // Adding the new Journal
                    collectionReference.add(journal).addOnSuccessListener {
                        binding.postProgressBar.visibility = View.INVISIBLE

                        val intent = Intent(this,JournalListActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }.addOnFailureListener {
                binding.postProgressBar.visibility = View.INVISIBLE
            }
        } else {
            binding.postProgressBar.visibility = View.INVISIBLE
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            data?.data?.let { uri ->

                imageUri = uri
                // Display the image in the ImageView
                binding.postImageview.setImageURI(uri)
            }
        }

//        if (resultCode == 1 && resultCode == RESULT_OK) {
//            if ( data != null) {
//                //Getting the actual image path
//                imageUri = data.data!!
//                // Showing the image
//                binding.postImageview.setImageURI(imageUri)
//            }
//        }
    }

    override fun onStart() {
        super.onStart()

        user = auth.currentUser!!
    }

    override fun onStop() {
        super.onStop()

        if(auth != null) {

        }
    }
}