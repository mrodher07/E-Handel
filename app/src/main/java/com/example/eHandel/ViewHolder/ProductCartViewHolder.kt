package com.example.eHandel.ViewHolder

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.eHandel.CartActivity
import com.example.eHandel.Models.Productos
import com.example.eHandel.ProductDetailsActivity
import com.example.eHandel.databinding.CartItemLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProductCartViewHolder(private val context: Context, vista:View):RecyclerView.ViewHolder(vista) {
    private val binding = CartItemLayoutBinding.bind(vista)
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    fun inflar(
        producto: Productos
    ){
        auth = FirebaseAuth.getInstance()
        binding.tvCartItemName.text = producto.name.toString().trim()
        binding.tvCartItemPrice.text = producto.price.toString()
        binding.tvCartItemQuantity.text = producto.quantity.toString()

        binding.rlCartItem.setOnClickListener {
            val intent = Intent(context, ProductDetailsActivity::class.java)
            intent.putExtra("name", producto.name.toString().trim())
            context.startActivity(intent)
        }
        binding.tvRemoveItemFormCart.setOnClickListener {
            val email = auth.currentUser?.email
            if(email != null){
                db.collection("cart").document(email)
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        if(documentSnapshot.exists()){
                            val gson = Gson()
                            val currentCartType = object : TypeToken<HashMap<String, Productos>>() {}.type
                            val currentCart = gson.fromJson<HashMap<String, Productos>>(
                                gson.toJson(documentSnapshot.data),
                                currentCartType
                            )
                            if(currentCart.containsKey(producto.name)){
                                currentCart.remove(producto.name)
                                db.collection("cart").document(email)
                                    .set(currentCart)
                                    .addOnSuccessListener {
                                        Log.d(ContentValues.TAG, "DocumentSnapshot successfully updated!")
                                        context.startActivity(Intent(context, CartActivity::class.java))
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e(ContentValues.TAG, "Error updating document", e)
                                        // Handle error during deletion
                                    }
                            }

                        }
                    }
            }
        }
    }
}