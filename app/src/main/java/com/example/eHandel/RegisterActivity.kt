package com.example.eHandel

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.eHandel.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvIsRegistered.setOnClickListener {
            intentInicioSesion()
        }

        binding.registerButton.setOnClickListener {
            var email = binding.registerEmailInput.text.toString().trim()
            var username = binding.registerUsernameInput.text.toString().trim()
            var country = binding.registerCountryInput.text.toString().trim()
            var direction = binding.registerDirectionInput.text.toString().trim()
            var phone = binding.registerPhoneInput.text.toString().trim()
            var password = binding.registerPasswordInput.text.toString().trim()
            register(email, username, country, direction, phone, password)
        }
    }

    companion object{
        private const val RC_SIGN_IN = 423
    }

    fun intentInicioSesion(){
        startActivity(Intent(this, MainActivity::class.java))
    }

    fun intentSuccessRegister(){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun register(email: String, username: String, country: String, direction: String, phone: String, password: String){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
            email, password
        ).addOnCompleteListener{
            if(it.isSuccessful){
                println("r "+email)
                writeNewUser(email, username, country, direction, phone, password)
            }else{
                Toast.makeText(this, "Los datos introducidos son erroneos compruebalo de nuevo.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun writeNewUser(email: String, username: String, country: String, direction: String, phone: String, password: String){
        val db = Firebase.firestore
        val data = hashMapOf(
            "email" to email,
            "username" to username,
            "country" to country,
            "direction" to direction,
            "phone" to phone,
            "password" to password,
            "isVendor" to false,
        )

        db.collection("user").document(email)
            .set(data)
            .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
                intentSuccessRegister()}
            .addOnFailureListener{ e -> println(e.message)}
    }
}