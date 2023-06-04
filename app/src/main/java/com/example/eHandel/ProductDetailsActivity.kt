package com.example.eHandel

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.eHandel.Models.Productos
import com.example.eHandel.databinding.ActivityProductDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProductDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailsBinding
    var cantidad = 0
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    var nombreProducto = ""
    var producto: Productos = Productos()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        loadData()

        listeners()
    }

    fun loadData(){
        val mibundle = intent.extras
        var name = mibundle?.getString("name").toString()
        val productRef = db.collection("products").document(name)
        productRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if(documentSnapshot.exists()) {
                    Picasso.get().load(documentSnapshot.getString("image")).into(binding.productDetailActivityImage)
                    binding.productDetailActivityName.text = documentSnapshot.getString("name")
                    binding.productDetailActivityPrice.text = "${binding.productDetailActivityPrice.text} ${documentSnapshot.getDouble("price")}€"
                    binding.productDetailActivityVendor.text = "${binding.productDetailActivityVendor.text} ${documentSnapshot.getString("vendor")}"
                    binding.productDetailActivityCantidad.text = "${binding.productDetailActivityCantidad.text} ${documentSnapshot.getLong("quantity")?.toInt()}"
                    binding.productDetailActivityType.text = "${binding.productDetailActivityType.text} ${documentSnapshot.getString("type")}"
                    cantidad = documentSnapshot.getLong("quantity")?.toInt()!!
                    nombreProducto = documentSnapshot.getString("name").toString()

                    producto.name = documentSnapshot.getString("name").toString()
                    producto.image = documentSnapshot.getString("image").toString()
                    producto.price = documentSnapshot.getDouble("price")!!
                    producto.vendor = documentSnapshot.getString("vendor").toString()
                    producto.type = documentSnapshot.getString("type").toString()
                }
            }

    }

    fun listeners(){

        binding.btnAddFromCartQuantity.setOnClickListener {
            if(binding.tvQuantityToAddToCart.text.toString().toInt() >= 0 && binding.tvQuantityToAddToCart.text.toString().toInt() < cantidad){
                binding.tvQuantityToAddToCart.text = (binding.tvQuantityToAddToCart.text.toString().toInt() + 1).toString()
            }
        }
        binding.btnRemoveFromCartQuantity.setOnClickListener {
            if(binding.tvQuantityToAddToCart.text.toString().toInt() > 0 && binding.tvQuantityToAddToCart.text.toString().toInt() <= cantidad){
                binding.tvQuantityToAddToCart.text = (binding.tvQuantityToAddToCart.text.toString().toInt() - 1).toString()
            }
        }

        binding.toolbarSettingsClose.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        binding.btnAddToCart.setOnClickListener {
            addToCart()
        }

    }

    fun addToCart(){
        var email = auth.currentUser?.email

        var hasMap = HashMap<String, Productos>()
        if (binding.tvQuantityToAddToCart.text.toString().toInt() > 0){
            producto.quantity = binding.tvQuantityToAddToCart.text.toString().toInt()
            if (email != null) {
                db.collection("cart").document(email)
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        val currentCartType = object : TypeToken<HashMap<String, Productos>>() {}.type
                        val gson = Gson()

                        val currentCart = if (documentSnapshot.exists()){
                            gson.fromJson<HashMap<String, Productos>>(gson.toJson(documentSnapshot.data), currentCartType)
                        }else{
                            HashMap<String, Productos>()
                        }

                        currentCart?.set(producto.name, producto)

                        if (currentCart != null) {
                            db.collection("cart").document(email)
                                .set(currentCart)
                                .addOnSuccessListener {
                                    Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
                                    Toast.makeText(this, "El producto ha sido añadido al carrito", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, HomeActivity::class.java))
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Log.e(ContentValues.TAG, "Error writing document", e)
                                }
                        }
                    }.addOnFailureListener { e ->
                        Log.e(ContentValues.TAG, "Error retrieving document", e)
                    }
            }
        }else{
            Toast.makeText(this, "Añade cualquier cantidad de este producto", Toast.LENGTH_SHORT).show()
        }

    }
}