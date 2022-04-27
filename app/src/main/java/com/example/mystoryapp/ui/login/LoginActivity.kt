package com.example.mystoryapp.ui.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.mystoryapp.databinding.ActivityLoginBinding
import com.example.mystoryapp.model.StoryModel
import com.example.mystoryapp.preferences.PrefViewModel
import com.example.mystoryapp.ui.home.HomeActivity
import com.example.mystoryapp.ui.register.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "account_settings")

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val prefViewModel: PrefViewModel by viewModels()

    private var _loginBinding: ActivityLoginBinding? = null
    private val binding get() = _loginBinding

    private val loginViewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupViewModel()

        binding?.apply {

            loginViewModel.isLoading.observe(this@LoginActivity) {
                showLoading(it)
            }

            btnLogin.setOnClickListener {
                val email = emailLogin.text.toString()
                val password = passwordLogin.text.toString()
//                mainViewModel.loginUser(email, password)

                when {
                    email.isEmpty() -> {
                        emailLogin.error = "Insert Email"
                    }
                    !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                        emailLogin.error = "Invalid Email"
                    }
                    password.isEmpty() && password.length < 6 -> {
                        passwordLogin.error = "Insert Password"
                    }
                    else -> {
                        loginViewModel.loginAccount(email, password)

                        setupLoginResult()

                    }
                }
            }

            tvBtnRegister.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }

        }
    }

    private fun setupViewModel() {
//        prefViewModel = ViewModelProvider(
//            this,
//            ViewModelFactory(AccountPreferences.getInstance(dataStore))
//        )[PrefViewModel::class.java]
    }

    private fun setupLoginResult() {
        loginViewModel.loginResult.observe(this@LoginActivity) { loginResult ->

            val intent = Intent(this@LoginActivity, HomeActivity::class.java)

            val token = "Bearer ${loginResult.token}"
            prefViewModel.saveInfo(
                StoryModel(
                    loginResult.name,
                    token,
                    true
                )
            )
            startActivity(intent)
            finish()
        }
    }


    private fun showLoading(state: Boolean) {
        if (state) binding?.progressBar?.visibility = View.VISIBLE
        else binding?.progressBar?.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _loginBinding = null
    }

}