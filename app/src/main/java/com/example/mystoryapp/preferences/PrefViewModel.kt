package com.example.mystoryapp.preferences

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mystoryapp.model.AccountPreferences
import com.example.mystoryapp.model.StoryModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrefViewModel @Inject constructor(private val pref: AccountPreferences) : ViewModel() {
    fun getInfo(): LiveData<StoryModel> {
        return pref.getInfo().asLiveData()
    }

    fun saveInfo(storyModel: StoryModel) {
        viewModelScope.launch {
            pref.saveInfo(storyModel)
        }
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }

}