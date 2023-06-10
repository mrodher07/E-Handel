package com.example.eHandel

import android.content.Intent
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.eHandel.adminActivities.AdminActivity
import com.example.eHandel.adminActivities.AdminAddNewProductActivity
import com.example.eHandel.databinding.ActivityMainBinding
import com.example.eHandel.info.exampleBottomSheet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    var dbName = "user"
    val db = Firebase.firestore
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
            iniciarSesionUsuario(email, password)
        }

        binding.tvImAdmin.setOnClickListener {
            binding.tvImAdmin.visibility = View.GONE
            binding.tvImNotAdmin.visibility = View.VISIBLE
            binding.loginButton.text = getString(R.string.loginAdminButtonText)
            dbName = "admin"
        }

        binding.tvImNotAdmin.setOnClickListener {
            binding.tvImAdmin.visibility = View.VISIBLE
            binding.tvImNotAdmin.visibility = View.GONE
            binding.loginButton.text = getString(R.string.loginButtonText)
            dbName = "user"
        }
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

    fun iniciarSesionUsuario(email: String, password: String){

        if(email == null || email == "" || password == null || password == ""){
            Toast.makeText(this, "Introduce valores validos.", Toast.LENGTH_SHORT).show()
        }else{
            FirebaseAuth.getInstance().signInWithEmailAndPassword(
                email, password
            ).addOnCompleteListener {
                if(it.isSuccessful){
                    if(dbName == "admin"){
                        val users = db.collection(dbName)
                        var isAdmin = false
                        users.whereEqualTo("email", email).whereEqualTo("password", password).get()
                            .addOnSuccessListener {
                                iniciarSesionAdmin()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "El usuario introducido no es admin", Toast.LENGTH_SHORT).show()
                            }
                    }else{
                        iniciarSesionUsuario();
                    }
                }else{
                    Toast.makeText(this, "Algun dato se ha introducido de forma incorrecta", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun iniciarSesionUsuario(){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun iniciarSesionAdmin(){
        val intent = Intent(this, AdminActivity::class.java)
        startActivity(intent)
        finish()
    }

}