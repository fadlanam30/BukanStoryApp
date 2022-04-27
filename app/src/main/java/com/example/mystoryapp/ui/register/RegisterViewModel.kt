package com.example.mystoryapp.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.mystoryapp.LoginResult
import com.example.mystoryapp.StoryResponse
import com.example.mystoryapp.data.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val storyRepository: StoryRepository) : ViewModel() {
    val storyResponse: LiveData<StoryResponse> = storyRepository.storyResponse

    val isLoading: LiveData<Boolean> = storyRepository.isLoading

    fun regisAccount(name: String, email: String, password: String) = storyRepository.registerAccount(name, email, password)
}