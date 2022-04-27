package com.example.mystoryapp.ui.home

import androidx.lifecycle.ViewModel
import com.example.mystoryapp.data.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val storyRepository: StoryRepository) : ViewModel() {

    fun getStory() = storyRepository.getStory()

}