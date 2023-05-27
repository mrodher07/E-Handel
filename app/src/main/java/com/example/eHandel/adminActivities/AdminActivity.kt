package com.example.eHandel.adminActivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.eHandel.HomeActivity
import com.example.eHandel.R
import com.example.eHandel.databinding.ActivityAdminAddNewProductBinding
import com.example.eHandel.databinding.ActivityAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminBinding
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        var email = auth.currentUser?.email
        val admins = db.collection("admin")
        val loggedAdmin = admins.document(email.toString())
        loggedAdmin.get()
            .addOnSuccessListener { document ->
                if(document != null) {
                    val username = document.getString("username")
                    binding.tvAdminName.text = username
                }
            }
            .addOnFailureListener { exception ->
                println(exception.toString())
            }

        binding.LinearLayoutAddNewProduct.setOnClickListener{
            val intent = Intent(this, AdminCategoryActivity::class.java)
            startActivity(intent)
        }
    }
}