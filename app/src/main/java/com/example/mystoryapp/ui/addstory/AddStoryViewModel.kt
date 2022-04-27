package com.example.mystoryapp.ui.addstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.mystoryapp.LoginResult
import com.example.mystoryapp.StoryResponse
import com.example.mystoryapp.data.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class AddStoryViewModel @Inject constructor(private val storyRepository: StoryRepository) : ViewModel() {
    val storyResponse: LiveData<StoryResponse> = storyRepository.storyResponse

    val isLoading: LiveData<Boolean> = storyRepository.isLoading

    fun uploadStory(token: String, imageMultipart: MultipartBody.Part, description: String) = storyRepository.uploadStory(token, imageMultipart, description)
}