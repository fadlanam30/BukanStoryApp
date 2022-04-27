package com.example.mystoryapp.ui.maps

import androidx.lifecycle.ViewModel
import com.example.mystoryapp.data.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GMapsViewModel @Inject constructor(private val storyRepository: StoryRepository) : ViewModel() {
    fun getStoryLocation() = storyRepository.getStoryLocation()
}