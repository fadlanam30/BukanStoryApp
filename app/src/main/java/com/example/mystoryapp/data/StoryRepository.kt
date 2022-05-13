package com.example.mystoryapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import retrofit2.Callback
import com.example.mystoryapp.ListStoryItem
import com.example.mystoryapp.LoginResult
import com.example.mystoryapp.StoryResponse
import com.example.mystoryapp.api.ApiService
import com.example.mystoryapp.model.AccountPreferences
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject

class StoryRepository @Inject constructor(
    private val apiService: ApiService,
    private val preferences: AccountPreferences
) {

    private val _storyResponse = MutableLiveData<StoryResponse>()
    val storyResponse: LiveData<StoryResponse> = _storyResponse

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loginAccount(email: String, password: String) {
        _isLoading.value = true
        val client = apiService.loginUser(email, password)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _loginResult.value = response.body()?.loginResult
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun registerAccount(name: String, email: String, password: String) {
        _isLoading.value = true
        val client = apiService.registerUser(name, email, password)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _storyResponse.value = response.body()
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun uploadStory(token: String, imageMultipart: MultipartBody.Part, description: String, lat: Double?, lon: Double?) {
        _isLoading.value = true
        val client = apiService.addStory(token, imageMultipart, description, lat, lon)
        client.enqueue(object : Callback<StoryResponse>{
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _storyResponse.value = response.body()
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

//    fun login(email: String, password:String) : LiveData<Result<LoginResult>> = liveData {
//        emit(Result.Loading)
//        try {
//            val response = apiService.login(email, password)
//
//            emit(Result.Success(response.loginResult))
//        } catch (exception: Exception) {
//            Log.d(TAG, "getStoryLocation: ${exception.message.toString()}")
//            emit(Result.Error(exception.message.toString()))
//        }
//    }

    fun getStoryLocation(): LiveData<Result<List<ListStoryItem>>> = liveData {
        emit(Result.Loading)
        try {
            val token = preferences.getInfo().first().token
            val response = apiService.getStoriesLoct(token)
            emit(Result.Success(response.listStory))
        } catch (exception: Exception) {
            Log.d(TAG, "getStoryLocation: ${exception.message.toString()}")
            emit(Result.Error(exception.message.toString()))
        }
    }

    fun getStory(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, preferences)
            }
        ).liveData
    }

    companion object {
        private const val TAG = "StoryRepository"
    }

}