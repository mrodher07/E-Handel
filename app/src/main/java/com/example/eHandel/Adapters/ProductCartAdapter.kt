package com.example.eHandel.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eHandel.Models.Productos
import com.example.eHandel.R
import com.example.eHandel.ViewHolder.ProductCartViewHolder
import com.example.eHandel.ViewHolder.ProductViewHolder

class ProductCartAdapter(private val context: Context, private val productList: List<Productos>): RecyclerView.Adapter<ProductCartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductCartViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cart_item_layout, parent, false)
        return ProductCartViewHolder(context, itemView)
    }

    override fun onBindViewHolder(holder: ProductCartViewHolder, position: Int) {
        holder.inflar(productList[position])
    }

    override fun getItemCount(): Int {
        return productList.size
    }


}