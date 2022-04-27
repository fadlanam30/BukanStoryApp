package com.example.mystoryapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.mystoryapp.LoginResult
import com.example.mystoryapp.data.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val storyRepository: StoryRepository) : ViewModel() {

    val loginResult: LiveData<LoginResult> = storyRepository.loginResult

    val isLoading: LiveData<Boolean> = storyRepository.isLoading

    fun loginAccount(email: String, password:String) = storyRepository.loginAccount(email, password)

}