package com.aswin.journalapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.aswin.journalapp.databinding.ActivityLoginBinding
import com.aswin.journalapp.singleton.JournalUser
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

/**
 * Email : aswin@gmail.com
 * password :123456
 * **/
class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    // Firebase Auth
    private lateinit var auth: FirebaseAuth


    // Firebase Connection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.btnCreateAccount.setOnClickListener {
            val Intent = Intent(this, SignupActivity::class.java)
            startActivity(Intent)
            finish()
        }

        binding.btnLogin.setOnClickListener {
            LoginWithEmailPassword(binding.edEmail.text.toString().trim(), binding.edPassword.text.toString().trim())
        }

        // Auth Ref
        auth = Firebase.auth

    }

    private fun LoginWithEmailPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Log.d("MainActivity", "signInUserWithEmail:success")
                    //val user = auth.currentUser

                    var  journal : JournalUser = JournalUser.instance!!
                    journal.userId = auth.currentUser?.uid
                    journal.username = auth.currentUser?.email

                    goToJournalList()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("MainActivity", "signInUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Login failed.", Toast.LENGTH_SHORT,).show()

                }
            }
    }


    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        // If logged in Go to home page(Journal List Activity)
        if(currentUser != null){
            goToJournalList()
        }
    }

    private fun goToJournalList() {
        val Intent = Intent(this, JournalListActivity::class.java)
        startActivity(Intent)
        finish()
    }

}