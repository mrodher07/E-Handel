package com.example.eHandel.ViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.eHandel.Models.Productos
import com.example.eHandel.databinding.ProductsLayoutBinding
import com.squareup.picasso.Picasso

class ProductViewHolder(vista: View):RecyclerView.ViewHolder(vista){
    private val binding = ProductsLayoutBinding.bind(vista)
    fun inflar(
        producto: Productos,
    ){
        binding.cardProductName.text = producto.name.toString().trim()
        Picasso.get().load(producto.image).into(binding.cardProductImage)
        binding.cardProductVendor.text = producto.vendor.toString().trim()
        binding.cardProductPrice.text = producto.price.toString() + "€"
    }

}