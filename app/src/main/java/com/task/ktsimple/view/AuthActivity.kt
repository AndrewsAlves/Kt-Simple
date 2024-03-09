package com.task.ktsimple.view

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.task.ktsimple.databinding.ActivityAuthBinding
import com.task.ktsimple.viewmodel.AuthViewModel
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.task.ktsimple.R
import com.task.ktsimple.adapters.RecyclerViewAdapter
import com.task.ktsimple.enums.AuthErrors
import com.task.ktsimple.enums.AuthState
import com.task.ktsimple.exceptions.AuthException
import com.task.ktsimple.viewmodel.LocationListViewModel

class AuthActivity : AppCompatActivity() {

    private lateinit var ui: ActivityAuthBinding

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(ui.root)

        ui.etUsername.setText(viewModel.userName.value)
        ui.etPassword.setText(viewModel.passWord.value)

        ui.etUsername.doOnTextChanged { text, start, before, count ->
            viewModel.updateUserName(text.toString())
        }

        ui.etPassword.doOnTextChanged { text, start, before, count ->
            viewModel.updatePassword(text.toString())
        }

        ui.btnRvSignin.setOnClickListener {
            viewModel.updateAuthState(AuthState.SIGN_IN)
        }

        ui.btnRvSignup.setOnClickListener {
            viewModel.updateAuthState(AuthState.SIGN_UP)
        }

        ui.btnRvLoginSignup.setOnClickListener {

            try {
                val user = viewModel.authUser()
                startActivity(Intent(this, LocationsListActivity::class.java))

            } catch (e : AuthException) {
                when(e.authErrors) {
                    AuthErrors.USERNAME_EXITS -> Snackbar.make(ui.root, "Username Already Exists!",Snackbar.LENGTH_SHORT).show()
                    AuthErrors.PASSWORD_MISMATCH -> Snackbar.make(ui.root, "Wrong Password!",Snackbar.LENGTH_SHORT).show()
                    AuthErrors.USERNAME_NOT_FOUND -> Snackbar.make(ui.root, "User not found!",Snackbar.LENGTH_SHORT).show()
                    AuthErrors.PASSWORD_TOO_SMALL -> Snackbar.make(ui.root, "Password is too small!",Snackbar.LENGTH_SHORT).show()
                    AuthErrors.USERNAME_TOO_SMALL -> Snackbar.make(ui.root, "Username is too small!",Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        // Recycler View
        ui.rvSwitchAccount.layoutManager = LinearLayoutManager(this)
        val adapter = RecyclerViewAdapter(this, viewModel.signedInUsers.value!!)
        ui.rvSwitchAccount.adapter = adapter

        // Observers
        viewModel.authState.observe(this)  {
            updateUi(it)
        }

        viewModel.signedInUsers.observe(this) {
            adapter.notifyItemInserted(it.size - 1)
        }

        ui.btnRvSignin.performClick()
    }

    fun updateUi(authState : AuthState) {

        ui.btnRvSignin.background = AppCompatResources.getDrawable(this,R.drawable.bg_border_stroke)
        ui.tvSignin.setTextColor(getColor(R.color.main))
        ui.btnRvSignup.background = AppCompatResources.getDrawable(this,R.drawable.bg_border_stroke)
        ui.tvSignup.setTextColor(getColor(R.color.main))

        when(authState) {
            AuthState.SIGN_IN -> {
                ui.btnRvSignin.background = AppCompatResources.getDrawable(this,R.drawable.bg_border)
                ui.tvSignin.setTextColor(getColor(R.color.white))
                ui.tvLoginSignup.text = "Sign In"
            }
            AuthState.SIGN_UP -> {
                ui.btnRvSignup.background = AppCompatResources.getDrawable(this,R.drawable.bg_border)
                ui.tvSignup.setTextColor(getColor(R.color.white))
                ui.tvLoginSignup.text = "Sign Up"
            }
        }
    }

    fun setUiForSignUp() {

    }
}