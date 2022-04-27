package com.example.mystoryapp.ui.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.mystoryapp.databinding.ActivityMainBinding
import com.example.mystoryapp.preferences.PrefViewModel
import com.example.mystoryapp.ui.home.HomeActivity
import com.example.mystoryapp.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import com.example.mystoryapp.ui.register.RegisterActivity as RegisterActivity1

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val prefViewModel:  PrefViewModel by viewModels()

    private var _activityMainBinding: ActivityMainBinding? = null
    private val binding get() = _activityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(3000)
        setupViewModel()
        installSplashScreen()
        _activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)




        binding?.apply {
            btnLogin.setOnClickListener {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            }
            btnRegister.setOnClickListener {
                startActivity(Intent(this@MainActivity, RegisterActivity1::class.java))
            }
        }

    }

    private fun playAnimation() {
        val login = ObjectAnimator.ofFloat(binding?.btnLogin, View.ALPHA, 1f).setDuration(700)
        val register = ObjectAnimator.ofFloat(binding?.btnRegister, View.ALPHA, 1f).setDuration(700)

        AnimatorSet().apply {
            playSequentially(login, register)
            start()
        }

    }

    private fun setupViewModel() {
//        prefViewModel = ViewModelProvider(
//            this,
//            ViewModelFactory(AccountPreferences.getInstance(dataStore))
//        )[PrefViewModel::class.java]
        prefViewModel.getInfo().observe(this) { story ->
            if (story.isLogin) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                playAnimation()
            }
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        _activityMainBinding = null
    }

}