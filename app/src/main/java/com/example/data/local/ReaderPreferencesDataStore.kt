package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "iqra_reader_prefs")

enum class ReaderThemeMode(val labelAr: String) {
    NIGHT_GLASS("زجاج ليلي"),
    SEPIA("ورق عتيق"),
    PARCHMENT("رق أبيض")
}

data class ReaderPreferences(
    val fontSizeSp: Float = 19f,
    val themeMode: ReaderThemeMode = ReaderThemeMode.NIGHT_GLASS,
    val isRealisticShelfMode: Boolean = true,
    val linkedPdfFolderUri: String? = null,
    val linkedPdfFolderName: String? = null
)

class ReaderPreferencesDataStore(private val context: Context) {

    private object Keys {
        val FONT_SIZE = floatPreferencesKey("reader_font_size")
        val THEME_MODE = stringPreferencesKey("reader_theme_mode")
        val REALISTIC_SHELF = booleanPreferencesKey("realistic_shelf_mode")
        val LINKED_FOLDER_URI = stringPreferencesKey("linked_pdf_folder_uri")
        val LINKED_FOLDER_NAME = stringPreferencesKey("linked_pdf_folder_name")
    }

    val preferences: Flow<ReaderPreferences> = context.dataStore.data.map { prefs ->
        val modeName = prefs[Keys.THEME_MODE] ?: ReaderThemeMode.NIGHT_GLASS.name
        val mode = runCatching { ReaderThemeMode.valueOf(modeName) }
            .getOrDefault(ReaderThemeMode.NIGHT_GLASS)
        ReaderPreferences(
            fontSizeSp = prefs[Keys.FONT_SIZE] ?: 19f,
            themeMode = mode,
            isRealisticShelfMode = prefs[Keys.REALISTIC_SHELF] ?: true,
            linkedPdfFolderUri = prefs[Keys.LINKED_FOLDER_URI],
            linkedPdfFolderName = prefs[Keys.LINKED_FOLDER_NAME]
        )
    }

    suspend fun setFontSize(sizeSp: Float) {
        context.dataStore.edit { it[Keys.FONT_SIZE] = sizeSp.coerceIn(15f, 28f) }
    }

    suspend fun setThemeMode(mode: ReaderThemeMode) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setShelfMode(isRealistic: Boolean) {
        context.dataStore.edit { it[Keys.REALISTIC_SHELF] = isRealistic }
    }

    suspend fun setLinkedPdfFolder(uri: String?, name: String?) {
        context.dataStore.edit { prefs ->
            if (uri == null) {
                prefs.remove(Keys.LINKED_FOLDER_URI)
                prefs.remove(Keys.LINKED_FOLDER_NAME)
            } else {
                prefs[Keys.LINKED_FOLDER_URI] = uri
                prefs[Keys.LINKED_FOLDER_NAME] = name ?: "مجلد PDF"
            }
        }
    }
}
