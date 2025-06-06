package com.js.games.ui.theme

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.themeDataStore by preferencesDataStore(name = "theme_preferences")

object ThemeDataStore {
    private val THEME_KEY = stringPreferencesKey("selected_theme")

    fun getTheme(context: Context): Flow<ThemeMode> {
        return context.themeDataStore.data.map { preferences ->
            when (preferences[THEME_KEY]) {
                ThemeMode.DARK.name -> ThemeMode.DARK
                ThemeMode.LIGHT.name -> ThemeMode.LIGHT
                else -> ThemeMode.SYSTEM
            }
        }
    }

    suspend fun saveTheme(context: Context, themeMode: ThemeMode) {
        context.themeDataStore.edit { preferences ->
            preferences[THEME_KEY] = themeMode.name
        }
    }
}
