package com.task.ktsimple.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import com.task.ktsimple.R
import com.task.ktsimple.databinding.ActivityLocationsListBinding
import com.task.ktsimple.model.User
import com.task.ktsimple.viewmodel.AuthViewModel
import com.task.ktsimple.viewmodel.LocationListViewModel

class LocationsListActivity : AppCompatActivity(), OnItemSelectedListener{

    private val viewModel: LocationListViewModel by viewModels()
    private lateinit var ui: ActivityLocationsListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityLocationsListBinding.inflate(layoutInflater)
        setContentView(ui.root)

        ui.btnIbBack.setOnClickListener {
            finish()
        }

        // Initilise Adapter
        Log.d("Locotion List", "length ${viewModel.signedInUserList.value!!.size}")
        val adapter = SpinnerAdapter(this, R.layout.item_spinner, viewModel.signedInUserList.value!!)
        adapter.setDropDownViewResource(R.layout.item_spinner)
        ui.spUserSpinner.adapter = adapter
        ui.spUserSpinner.onItemSelectedListener = this

        viewModel.currentUser.observe(this) {

        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedItem = parent?.getItemAtPosition(position) as User
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    /**
     *
     *
     * ADAPTER
     *
     *
     * **/

    private class SpinnerAdapter(
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
            val view = super.getView(position, convertView, parent)
            val item = getItem(position)
            if (item != null) {
                view.findViewById<TextView>(R.id.tv_username).text = item.userName
                view.findViewById<ImageView>(R.id.iv_avatar).setImageDrawable(context.getDrawable(avatarList[item.avatar]))
            }
            return view
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getDropDownView(position, convertView, parent)
            val item = getItem(position)
            if (item != null) {
                view.findViewById<TextView>(R.id.tv_username).text = item.userName
                view.findViewById<ImageView>(R.id.iv_avatar).setImageDrawable(context.getDrawable(avatarList[item.avatar]))
            }
            return view
        }
    }
}