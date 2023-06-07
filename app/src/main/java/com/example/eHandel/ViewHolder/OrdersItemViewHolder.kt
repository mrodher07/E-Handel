package com.example.eHandel.ViewHolder

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.eHandel.CartActivity
import com.example.eHandel.Models.Order
import com.example.eHandel.OrderProductDetails
import com.example.eHandel.databinding.OrdersItemLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OrdersItemViewHolder(private val context: Context, vista: View):RecyclerView.ViewHolder(vista) {
    private val binding = OrdersItemLayoutBinding.bind(vista)
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    fun inflar(
        id: String,
        order: Order
    ){
        auth = FirebaseAuth.getInstance()
        binding.tvOrderId.text = id
        binding.tvOrderName.text = order.name
        binding.tvOrderAddress.text = order.address
        binding.tvOrderCityName.text = order.cityName
        binding.tvOrderPhoneNumber.text = order.phoneNumber

        binding.rlOrderItem.setOnClickListener {
            val intent = Intent(context, OrderProductDetails::class.java)
            intent.putExtra("id", id)
            context.startActivity(intent)
        }

    }


}