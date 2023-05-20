package com.example.eHandel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.eHandel.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.tvNotRegistered.setOnClickListener {
            intentRegistro()
        }

        binding.loginButton.setOnClickListener {
            var email = binding.loginEmailInput.text.toString().trim()
            var password = binding.loginPasswordInput.text.toString().trim()
            iniciarSesion(email, password)
        }
    }

    override fun onStart() {
        super.onStart()
        if(auth.currentUser != null){
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    companion object{
        private const val RC_SIGN_IN = 423
    }

    fun intentRegistro(){
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    fun iniciarSesion(email: String, password: String){

        if(email == null || email == "" || password == null || password == ""){
            Toast.makeText(this, "Introduce valores validos.", Toast.LENGTH_SHORT).show()
        }else{
            FirebaseAuth.getInstance().signInWithEmailAndPassword(
                email, password
            ).addOnCompleteListener {
                if(it.isSuccessful){
                    iniciarSesion();
                }else{
                    Toast.makeText(this, "Algun dato se ha introducido de forma incorrecta", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun iniciarSesion(){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

}