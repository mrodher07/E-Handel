package com.example.eHandel.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eHandel.Models.Order
import com.example.eHandel.R
import com.example.eHandel.ViewHolder.OrdersItemViewHolder

class OrdersItemAdapter(private val context: Context, private val orderList: MutableMap<String, Order>): RecyclerView.Adapter<OrdersItemViewHolder>(){

    private val keys: List<String> = orderList.keys.toList()
    private val orders: List<Order> = orderList.values.toList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.orders_item_layout, parent, false)
        return OrdersItemViewHolder(context, itemView)
    }

    override fun onBindViewHolder(holder: OrdersItemViewHolder, position: Int) {
        val id = keys[position]
        val order= orders[position]
        holder.inflar(id, order)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }
}