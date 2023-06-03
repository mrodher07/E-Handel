package com.example.eHandel

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eHandel.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.content.Context
import com.example.eHandel.Adapters.ProductAdapter
import com.example.eHandel.Models.Productos

class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appBarHome.toolbar.setTitle(R.string.homeTitle)
        setSupportActionBar(binding.appBarHome.toolbar)

        binding.appBarHome.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_home)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.nav_cart -> {

                }
                R.id.nav_sellProduct -> {

                }
                R.id.nav_orders -> {

                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
                R.id.nav_logout -> {
                    auth = FirebaseAuth.getInstance()
                    auth.signOut()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
            true
        }

        loadUserData()
        createRecyclerViewProducts()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun loadUserData(){
        auth = FirebaseAuth.getInstance()
        var email = auth.currentUser?.email
        val users = db.collection("user")
        val loggedUser = users.document(email.toString())
        loggedUser.get()
            .addOnSuccessListener { document ->
                if(document != null) {
                    val headerView = binding.navView.getHeaderView(0)
                    val tvUsername = headerView.findViewById<TextView>(R.id.tvUsernameNavHeaderHome)
                    tvUsername.text = document.getString("username")
                    val tvEmail = headerView.findViewById<TextView>(R.id.tvEmailNavHeaderHome)
                    tvEmail.text = document.getString("email")
                }
            }
    }

    fun createRecyclerViewProducts(){
        var recyclerView = findViewById<RecyclerView>(R.id.rvProductList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        var productList: List<Productos> = mutableListOf()
        val db = FirebaseFirestore.getInstance()
        val productsCollection = db.collection("products")
        productsCollection.get()
            .addOnSuccessListener { documents ->
                var mutableList = productList.toMutableList()
                for (document in documents) {
                    val product = document.toObject(Productos::class.java)
                    mutableList.add(product)
                }
                productList = mutableList.toList()

                val adapter = ProductAdapter(productList)
                recyclerView.adapter = adapter
            }
    }

}