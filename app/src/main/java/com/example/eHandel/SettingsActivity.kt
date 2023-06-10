package com.example.eHandel

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.eHandel.databinding.ActivitySettingsBinding
import com.example.eHandel.info.exampleBottomSheet
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menubar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.menuApp->{
                val bottomSheet = exampleBottomSheet(R.layout.activity_app_info)
                bottomSheet.show(supportFragmentManager, bottomSheet.tag)
            }

            R.id.menuAcercaDe->{
                val bottomSheet = exampleBottomSheet(R.layout.activity_info)
                bottomSheet.show(supportFragmentManager, bottomSheet.tag)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val menuItem1 = menu?.findItem(R.id.menuAcercaDe)
        if (menuItem1 != null) {
            menuItem1.icon?.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN)
        }
        val menuItem2 = menu?.findItem(R.id.menuApp)
        if (menuItem2 != null) {
            menuItem2.icon?.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN)
        }

        return super.onPrepareOptionsMenu(menu)
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
        var currentUser = auth.currentUser
        var email = currentUser?.email
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

                            if (currentUser != null) {
                                currentUser.updatePassword(binding.etSettingsPassword.text.toString())
                                    .addOnSuccessListener {
                                        auth = FirebaseAuth.getInstance()
                                        auth.signOut()
                                        startActivity(Intent(this, MainActivity::class.java))
                                        finish()
                                        Toast.makeText(this, "Debes iniciar sesión de nuevo para que tus cambios se completen", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener{ e -> println(e.message)}
                            }
                        }
                        .addOnFailureListener{ e -> println(e.message)}
                }
            }


    }
}