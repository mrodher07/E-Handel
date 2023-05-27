package com.example.eHandel.adminActivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.eHandel.MainActivity
import com.example.eHandel.databinding.ActivityAdminAddNewProductBinding
import com.google.firebase.auth.FirebaseAuth

class AdminAddNewProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminAddNewProductBinding
    private lateinit var auth: FirebaseAuth
    private var tipo = "";
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminAddNewProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recogerTipo()

        // Esto es provisional quitar cuando se empiece a crear la activity
        auth = FirebaseAuth.getInstance()
        var nombre = auth.currentUser?.email
        Toast.makeText(this, "Ha entrado el ADMIN", Toast.LENGTH_SHORT).show()
        auth.signOut()
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun recogerTipo(){
        val mibundle = intent.extras
        tipo = mibundle?.getString("type").toString()
    }
}