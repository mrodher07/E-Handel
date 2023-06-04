package com.example.eHandel.ViewHolder

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.eHandel.Models.Productos
import com.example.eHandel.ProductDetailsActivity
import com.example.eHandel.databinding.CartItemLayoutBinding

class ProductCartViewHolder(private val context: Context, vista:View):RecyclerView.ViewHolder(vista) {
    private val binding = CartItemLayoutBinding.bind(vista)
    fun inflar(
        producto: Productos
    ){
        binding.tvCartItemName.text = producto.name.toString().trim()
        binding.tvCartItemPrice.text = producto.price.toString()
        binding.tvCartItemQuantity.text = producto.quantity.toString()

        binding.rlCartItem.setOnClickListener {
            val intent = Intent(context, ProductDetailsActivity::class.java)
            intent.putExtra("name", producto.name.toString().trim())
            context.startActivity(intent)
        }
    }
}