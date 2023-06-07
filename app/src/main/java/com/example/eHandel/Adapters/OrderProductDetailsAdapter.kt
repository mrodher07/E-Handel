package com.example.eHandel.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eHandel.Models.Productos
import com.example.eHandel.R
import com.example.eHandel.ViewHolder.OrdersProductDetailsViewHolder

class OrderProductDetailsAdapter(private val context: Context, private val productList: HashMap<String, Productos>): RecyclerView.Adapter<OrdersProductDetailsViewHolder>() {

    private val keys: List<String> = productList.keys.toList()
    private val productos: List<Productos> = productList.values.toList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrdersProductDetailsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.order_product_layout, parent, false)
        return OrdersProductDetailsViewHolder(context, itemView)
    }

    override fun onBindViewHolder(holder: OrdersProductDetailsViewHolder, position: Int) {
        println(productos[position].quantity)
        holder.inflar(productos[position])
    }

    override fun getItemCount(): Int {
        return productList.size
    }


}