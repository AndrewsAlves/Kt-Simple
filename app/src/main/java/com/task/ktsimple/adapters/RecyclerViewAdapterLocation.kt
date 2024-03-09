package com.task.ktsimple.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.task.ktsimple.R
import com.task.ktsimple.databinding.ItemLocationBinding
import com.task.ktsimple.databinding.ItemProfilesBinding
import com.task.ktsimple.interfaces.ItemClickedListener
import com.task.ktsimple.model.Location
import com.task.ktsimple.model.User

/**
 * Created by Admin on 08,March,2024
 */

class RecyclerViewAdapterLocation(val context : Context, private val dataList: List<Location>) :
    RecyclerView.Adapter<RecyclerViewAdapterLocation.MyViewHolder>() {

    var itemClickedListener : ItemClickedListener? = null

    class MyViewHolder(itemView: ItemLocationBinding) : RecyclerView.ViewHolder(itemView.root) {
        var ui = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.ui.apply {
            tvLocationAdd.text = dataList[position].address
            tvLatLog.text = dataList[position].lat.toString() + " - " + dataList[position].lon.toString()
            root.setOnClickListener {
                itemClickedListener?.itemClicked(position, dataList[position])
            }
        }
    }

    fun setClickedListener(itemClickedListener : ItemClickedListener) {
        this.itemClickedListener = itemClickedListener
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}