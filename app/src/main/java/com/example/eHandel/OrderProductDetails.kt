package com.example.eHandel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eHandel.Adapters.OrderProductDetailsAdapter
import com.example.eHandel.Adapters.OrdersItemAdapter
import com.example.eHandel.Models.Order
import com.example.eHandel.Models.Productos
import com.example.eHandel.databinding.ActivityOrderProductDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OrderProductDetails : AppCompatActivity() {
    private lateinit var binding: ActivityOrderProductDetailsBinding
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    var id = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.toolbarSettingsClose.setOnClickListener {
            startActivity(Intent(this, OrdersActivity::class.java))
            finish()
        }
        loadData()
        createRecyclerViewOrderProducts()
    }

    fun createRecyclerViewOrderProducts(){
        var recyclerView = findViewById<RecyclerView>(R.id.rvOrdersProducts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val email = auth.currentUser?.email

        if(email != null){
            val orderRef = db.collection("orders").document(email)
            orderRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()){
                        val currentOrders = document.data

                        if(currentOrders != null){
                            val orderMap = HashMap<String, Order>()
                            for ((key, value) in currentOrders){
                                val order = value as? HashMap<*, *>
                                if(order != null){
                                    val orderId = key
                                    val name = order["name"] as? String ?: ""
                                    val phoneNumber = order["phoneNumber"] as? String ?: ""
                                    val address = order["address"] as? String ?: ""
                                    val cityName = order["cityName"] as? String ?: ""
                                    val country = order["country"] as? String ?: ""
                                    val productsData = order["products"] as? HashMap<*, *>
                                    val products = HashMap<String, Productos>()

                                    if (productsData != null){
                                        for ((productId, productValue) in productsData){
                                            val productData = productValue as? HashMap<*, *>
                                            if(productData != null){
                                                val productImage = productData["image"] as? String ?: ""
                                                val productName = productData["name"] as? String ?: ""
                                                val productPrice = productData["price"] as? Double ?: 0.0
                                                val quantity = productData["quantity"] as? Long
                                                val productQuantity = quantity?.toInt() ?:0
                                                println(productQuantity)
                                                val productType = productData["Type"] as? String ?: ""
                                                val productVendor = productData["vendor"] as? String ?: ""
                                                val producto = Productos(
                                                    productImage,
                                                    productName,
                                                    productPrice,
                                                    productQuantity,
                                                    productType,
                                                    productVendor
                                                )
                                                products[productId.toString()] = producto
                                            }
                                        }
                                    }

                                    val orderObject = Order(
                                        name,
                                        phoneNumber,
                                        address,
                                        cityName,
                                        country,
                                        products
                                    )
                                    orderMap[orderId] = orderObject
                                }
                            }
                            println(orderMap)
                            val productList = orderMap[id]?.products
                            println(productList)
                            val adapter = OrderProductDetailsAdapter(this, productList!!)
                            recyclerView.adapter = adapter
                        }
                    }
                }
        }

    }

    fun loadData(){
        val mibundle = intent.extras
        id = mibundle?.getString("id").toString()
    }
}