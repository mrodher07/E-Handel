package com.example.eHandel

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.eHandel.Models.Order
import com.example.eHandel.Models.Productos
import com.example.eHandel.databinding.ActivityConfirmFinalOrderBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random

class ConfirmFinalOrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConfirmFinalOrderBinding
    private lateinit var auth: FirebaseAuth
    private val random = ThreadLocalRandom.current()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        binding = ActivityConfirmFinalOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnConfirmOrder.setOnClickListener {
            confirmOrder()
        }
    }

    fun confirmOrder(){
        var email = auth.currentUser?.email

        var name = binding.etConfirmOrderName.text
        var phone = binding.etConfirmPhoneNumber.text
        var address = binding.etConfirmAddress.text
        var cityName = binding.etConfirmCityName.text

        var products: HashMap<String,Productos>

        val firestoreDB = FirebaseFirestore.getInstance()
        val db = Firebase.firestore
        db.collection("user").document(email!!)
            .get()
            .addOnSuccessListener { userDocument ->
                db.collection("cart").document(email)
                    .get()
                    .addOnSuccessListener { cartDocument ->
                        val currentCart = if (cartDocument.exists()) {
                            cartDocument.data?.toMutableMap() ?: mutableMapOf()
                        } else {
                            mutableMapOf()
                        }

                        val order = Order().apply {
                            this.name = name.toString()
                            this.phoneNumber = phone.toString()
                            this.address = address.toString()
                            this.cityName = cityName.toString()
                            this.country = userDocument.getString("country").toString()
                            this.products = currentCart as HashMap<String, Productos>
                        }

                        val newOrder = HashMap<String, Order>()

                        db.collection("orders").document(email)
                            .get()
                            .addOnSuccessListener { orderDocument ->
                                val currentOrders = if (orderDocument.exists()) {
                                    orderDocument.data?.toMutableMap() ?: mutableMapOf()
                                } else {
                                    mutableMapOf()
                                }

                                val idAleatorio = generateRandomNumber()

                                currentOrders[idAleatorio.toString()] = order

                                if (currentOrders.isNotEmpty()) {
                                    db.collection("orders").document(email)
                                        .set(currentOrders)
                                        .addOnSuccessListener {
                                            Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
                                            Toast.makeText(this, "El pedido ha sido añadido", Toast.LENGTH_SHORT).show()
                                            startActivity(Intent(this, HomeActivity::class.java))
                                            finish()
                                            db.collection("cart").document(email).delete().addOnSuccessListener {
                                                println("Se ha borrado correctamente")
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e(ContentValues.TAG, "Error writing document", e)
                                        }
                                }
                            }
                    }
            }
    }
    fun generateRandomNumber(): Int {
        val random = random.nextInt()
        return if (random < 0) -random else random
    }
}

