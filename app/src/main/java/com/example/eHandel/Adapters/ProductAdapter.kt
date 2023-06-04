package com.example.eHandel.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eHandel.Models.Productos
import com.example.eHandel.R
import com.example.eHandel.ViewHolder.ProductViewHolder
import android.content.Context

class ProductAdapter(private val context: Context, private val productList: List<Productos>): RecyclerView.Adapter<ProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.products_layout, parent, false)
        return ProductViewHolder(context, itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.inflar(productList[position])
    }

    override fun getItemCount(): Int {
        return productList.size
    }

}