package com.example.eHandel

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.eHandel.databinding.ActivitySettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    var isVendor = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarSettingsClose.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        binding.toolbarSettingsUpdate.setOnClickListener {
            updateUserData()
        }

        binding.btnSettingsIsVendor.setOnClickListener {
            if(isVendor != true){
                binding.btnSettingsIsVendor.setBackgroundColor(Color.parseColor("#0CB10F"))
                binding.btnSettingsIsVendor.setText(R.string.btnSettingsIsVendorTrue)
                isVendor = true
            }else{
                binding.btnSettingsIsVendor.setBackgroundColor(Color.parseColor("#b32317"))
                binding.btnSettingsIsVendor.setText(R.string.btnSettingsIsVendorFalse)
                isVendor = false
            }
        }

        loadUserData()
    }

    fun loadUserData(){
        auth = FirebaseAuth.getInstance()
        var email = auth.currentUser?.email
        val users = db.collection("user")
        val loggedUser = users.document(email.toString())
        loggedUser.get()
            .addOnSuccessListener { document ->
                if(document != null) {
                    binding.profileUsernameChange.text = document.getString("username")
                    binding.profileEmailChange.text = document.getString("email")
                    binding.etSettingsPhoneNumber.setText(document.getString("phone").toString())
                    binding.etSettingsAddress.setText(document.getString("direction").toString())
                    binding.etSettingsCountry.setText(document.getString("country").toString())
                    if(document.getBoolean("isVendor") == true){
                        binding.btnSettingsIsVendor.setBackgroundColor(Color.parseColor("#0CB10F"))
                        binding.btnSettingsIsVendor.setText(R.string.btnSettingsIsVendorTrue)
                        isVendor = true
                    }else{
                        binding.btnSettingsIsVendor.setBackgroundColor(Color.parseColor("#b32317"))
                        binding.btnSettingsIsVendor.setText(R.string.btnSettingsIsVendorFalse)
                        isVendor = false
                    }
                    binding.etSettingsPassword.setText(document.getString("password"))
                }
            }
    }

    fun updateUserData(){

        auth = FirebaseAuth.getInstance()
        var email = auth.currentUser?.email
        val users = db.collection("user")
        val loggedUser = users.document(email.toString())
        loggedUser.get()
            .addOnSuccessListener { document ->
                if(document != null) {
                    val db = Firebase.firestore
                    val data = hashMapOf(
                        "email" to document.getString("email"),
                        "username" to document.getString("username"),
                        "country" to binding.etSettingsCountry.text.toString(),
                        "direction" to binding.etSettingsAddress.text.toString(),
                        "phone" to binding.etSettingsPhoneNumber.text.toString(),
                        "password" to binding.etSettingsPassword.text.toString(),
                        "isVendor" to isVendor,
                    )

                    loggedUser.update(data as Map<String, Any>)
                        .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener{ e -> println(e.message)}
                }
            }


    }
}