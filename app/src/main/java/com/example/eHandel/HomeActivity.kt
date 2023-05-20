package com.example.eHandel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.eHandel.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        // Esto es provisional quitar cuando se empiece a crear la activity
        auth = FirebaseAuth.getInstance()
        var nombre = auth.currentUser?.email
        Toast.makeText(this, nombre, Toast.LENGTH_SHORT).show()
        auth.signOut()
        startActivity(Intent(this, MainActivity::class.java))
    }
}