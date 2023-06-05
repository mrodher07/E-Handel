package com.example.eHandel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.eHandel.Adapters.ProductAdapter
import com.example.eHandel.Adapters.ProductCartAdapter
import com.example.eHandel.Models.Productos
import com.example.eHandel.databinding.ActivityCartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        createRecyclerViewCart()

        binding.toolbarSettingsClose.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        binding.buttonNextCart.setOnClickListener {
            startActivity(Intent(this, ConfirmFinalOrderActivity::class.java))
        }
    }

    fun createRecyclerViewCart(){
        var precioTotal = 0.00
        var recyclerView = findViewById<RecyclerView>(R.id.rvProductInCart)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val email = auth.currentUser?.email

        if (email != null){
            val cartRef = db.collection("cart").document(email)
            cartRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val gson = Gson()
                        val currentCartType = object : TypeToken<HashMap<String, Productos>>() {}.type
                        val currentCart = gson.fromJson<HashMap<String, Productos>>(
                            gson.toJson(documentSnapshot.data),
                            currentCartType
                        )

                        if(currentCart.isEmpty()){
                            binding.rvProductInCart.visibility = View.GONE
                            binding.tvNoProductsInCart.visibility = View.VISIBLE
                            binding.tvTotalPriceCart.text = "${binding.tvTotalPriceCart.text} 0€"
                        }else{
                            val productList = currentCart.values.toList()
                            val adapter = ProductCartAdapter(this, productList)
                            recyclerView.adapter = adapter
                            binding.rvProductInCart.visibility = View.VISIBLE
                            binding.tvNoProductsInCart.visibility = View.GONE
                            for (product in productList){
                                precioTotal += (product.quantity * product.price)
                            }
                            binding.tvTotalPriceCart.text = "${binding.tvTotalPriceCart.text} ${precioTotal}€"
                        }
                    }else{
                        binding.rvProductInCart.visibility = View.GONE
                        binding.tvNoProductsInCart.visibility = View.VISIBLE
                    }
                }
        }
    }
}