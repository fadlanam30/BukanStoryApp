package com.example.mystoryapp.model

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.mystoryapp.ui.login.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class AccountPreferences @Inject constructor(@ApplicationContext val context: Context) {

    private val dataStore = context.dataStore

    fun getInfo(): Flow<StoryModel> {
        return dataStore.data.map { preferences ->
            StoryModel(
                preferences[NAME_KEY] ?: "",
                preferences[TOKEN_KEY] ?: "",
                preferences[LOGIN_KEY] ?: false
            )
        }
    }

    suspend fun saveInfo(storyModel: StoryModel) {
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = storyModel.name
            preferences[TOKEN_KEY] = storyModel.token
            preferences[LOGIN_KEY] = storyModel.isLogin
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[LOGIN_KEY] = false
        }
    }

    companion object {
//        @Volatile
//        private var INSTANCE: AccountPreferences? = null

        private val NAME_KEY = stringPreferencesKey("name")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val LOGIN_KEY = booleanPreferencesKey("state")

//        fun getInstance(dataStore: DataStore<Preferences>): AccountPreferences {
//            return INSTANCE ?: synchronized(this) {
//                val instance = AccountPreferences(dataStore)
//                INSTANCE = instance
//                instance
//            }
//        }

    }

}