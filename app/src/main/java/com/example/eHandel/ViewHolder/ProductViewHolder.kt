package com.example.eHandel.ViewHolder

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.eHandel.MainActivity
import com.example.eHandel.Models.Productos
import com.example.eHandel.ProductDetailsActivity
import com.example.eHandel.adminActivities.AdminAddNewProductActivity
import com.example.eHandel.databinding.ActivityProductDetailsBinding
import com.example.eHandel.databinding.ProductsLayoutBinding
import com.squareup.picasso.Picasso

class ProductViewHolder(private val context: Context, vista: View):RecyclerView.ViewHolder(vista){
    private val binding = ProductsLayoutBinding.bind(vista)
    fun inflar(
        producto: Productos,
    ){
        binding.cardProductName.text = producto.name.toString().trim()
        Picasso.get().load(producto.image).into(binding.cardProductImage)
        binding.cardProductVendor.text = producto.vendor.toString().trim()
        binding.cardProductPrice.text = producto.price.toString() + "€"

        binding.rlCardProduct.setOnClickListener {
            val intent = Intent(context, ProductDetailsActivity::class.java)
            intent.putExtra("name", producto.name.toString().trim())
            context.startActivity(intent)
        }

    }
}