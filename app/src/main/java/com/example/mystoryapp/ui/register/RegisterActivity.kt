package com.example.mystoryapp.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.example.mystoryapp.databinding.ActivityRegisterBinding
import com.example.mystoryapp.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private var _registerBinding: ActivityRegisterBinding? = null
    private val binding get() = _registerBinding

    private val registerViewModel by viewModels<RegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.apply {

            registerViewModel.isLoading.observe(this@RegisterActivity) {
                showLoading(it)
            }

            btnRegister.setOnClickListener {
                val name = nameRegis.text.toString()
                val email = emailRegis.text.toString()
                val password = passwordRegis.text.toString()
                val confpassword = confpasswordRegis.text.toString()

                when {
                    name.isEmpty() -> {
                        nameRegis.error = "Insert Name"
                    }
                    email.isEmpty() -> {
                        emailRegis.error = "Insert Email"
                    }
                    !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                        emailRegis.error = "Invalid Email"
                    }
                    password.isEmpty() && password.length < 6 -> {
                        passwordRegis.error = "Insert Password"
                    }
                    confpassword.isEmpty() -> {
                        confpasswordRegis.error = "Insert Confirm Password"
                    }
                    password != confpassword -> {
                        confpasswordRegis.error = "Confirm password does not match"
                    }
                    else -> {
                        registerViewModel.regisAccount(name, email, password)
                        AlertDialog.Builder(this@RegisterActivity).apply {
                            setTitle("Yeah!")
                            setMessage("Your Account has been created.")
                            setPositiveButton("Next") { _, _ ->
                                finish()
                            }
                            create()
                            show()
                        }
                    }
                }

            }

            tvBtnLogin.setOnClickListener {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            }
        }

    }

    private fun showLoading(state: Boolean) {
        if (state) binding?.progressBar?.visibility = View.VISIBLE
        else binding?.progressBar?.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _registerBinding = null
    }
}