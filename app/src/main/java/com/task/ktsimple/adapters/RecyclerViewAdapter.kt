package com.task.ktsimple.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.task.ktsimple.R
import com.task.ktsimple.databinding.ItemProfilesBinding
import com.task.ktsimple.interfaces.ItemClickedListener
import com.task.ktsimple.model.User

/**
 * Created by Admin on 08,March,2024
 */
class RecyclerViewAdapter(val context : Context, private val dataList: List<User>) : RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

    val avatarList = listOf<Int>(R.drawable.img_avatar_1,
        R.drawable.img_avatar_2,
        R.drawable.img_avatar_3,
        R.drawable.img_avatar_4,
        R.drawable.img_avatar_5,
        R.drawable.img_avatar_6)

    var itemClickedListener : ItemClickedListener? = null

    class MyViewHolder(itemView: ItemProfilesBinding) : RecyclerView.ViewHolder(itemView.root) {
        var ui = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemProfilesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.ui.apply {
            tvUsername.text = dataList[position].userName
            ivAvatar.setImageDrawable(context.getDrawable(avatarList[dataList[position].avatar]))
            root.setOnClickListener {
                itemClickedListener?.itemClicked(position, dataList[position])
            }
            btnTvSignout.setOnClickListener {
                itemClickedListener?.clickedSignOut()
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