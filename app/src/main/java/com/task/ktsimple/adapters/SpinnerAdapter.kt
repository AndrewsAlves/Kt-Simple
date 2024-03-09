package com.task.ktsimple.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.task.ktsimple.R
import com.task.ktsimple.model.User

/**
 * Created by Admin on 09,March,2024
 */
class SpinnerAdapter(
    context: AppCompatActivity,
    private val resource: Int,
    private val objects: List<User>
) : ArrayAdapter<User>(context, resource, objects) {

    val avatarList = listOf<Int>(
        R.drawable.img_avatar_1,
        R.drawable.img_avatar_2,
        R.drawable.img_avatar_3,
        R.drawable.img_avatar_4,
        R.drawable.img_avatar_5,
        R.drawable.img_avatar_6)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(R.layout.item_spinner, parent, false)
        val item = getItem(position)
        if (item != null) {
            view.findViewById<TextView>(R.id.tv_username).text = item.userName
            view.findViewById<ImageView>(R.id.iv_avatar).setImageDrawable(context.getDrawable(avatarList[item.avatar]))
        }
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(R.layout.item_spinner, parent, false)
        val item = getItem(position)
        if (item != null) {
            view.findViewById<TextView>(R.id.tv_username).text = item.userName
            view.findViewById<ImageView>(R.id.iv_avatar).setImageDrawable(context.getDrawable(avatarList[item.avatar]))
        }
        return view
    }
}