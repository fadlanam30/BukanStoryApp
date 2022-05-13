package com.example.mystoryapp.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mystoryapp.databinding.ActivityHomeBinding
import com.example.mystoryapp.preferences.PrefViewModel
import com.example.mystoryapp.ui.main.MainActivity
import com.example.mystoryapp.ui.adapter.ListStoryAdapter
import com.example.mystoryapp.ui.adapter.LoadingStateAdapter
import com.example.mystoryapp.ui.addstory.AddStoryActivity
import com.example.mystoryapp.ui.maps.GoogleMapsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val prefViewModel:  PrefViewModel by viewModels()

    private val homeViewModel by viewModels<HomeViewModel>()

    private var _homeBinding: ActivityHomeBinding? = null
    private val binding get() = _homeBinding

    private lateinit var listStoryAdapter: ListStoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _homeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        listStoryAdapter = ListStoryAdapter()



        setupListData()
        showUserList()
        setupViewModel()
        setupActionButton()

    }

    override fun onResume() {
        super.onResume()
        setupListData()

    }

    private fun setupListData() {binding?.rvStory?.adapter = listStoryAdapter.withLoadStateFooter(
        footer = LoadingStateAdapter {
            listStoryAdapter.retry()
        }
    )

        homeViewModel.getStory().observe(this) {
            listStoryAdapter.submitData(lifecycle, it)
        }
    }

    private fun showUserList() {
        binding?.apply {
            rvStory.setHasFixedSize(true)
            rvStory.layoutManager = LinearLayoutManager(this@HomeActivity)
        }

    }

    private fun setupActionButton() {
        binding?.apply {
            btnAddNew.setOnClickListener {
                startActivity(Intent(this@HomeActivity, AddStoryActivity::class.java))
            }

            btnMaps.setOnClickListener {
                startActivity(Intent(this@HomeActivity, GoogleMapsActivity::class.java))
            }

            btnLogout.setOnClickListener {
                prefViewModel.logout()
            }
        }

    }

    private fun setupViewModel() {

        prefViewModel.getInfo().observe(this) { story ->

            if (story.isLogin) {
                Toast.makeText(this, "Hallo ${story.name}", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                Toast.makeText(this, "You have been logout.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun showLoading(state: Boolean) {
        if (state) binding?.progressBar?.visibility = View.VISIBLE
        else binding?.progressBar?.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _homeBinding = null
    }

}