package com.example.taskscheduler.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.io.IOException
import androidx.datastore.preferences.core.Preferences
import kotlin.collections.get

// Top-level property to create the DataStore instance (once per app process)
// The name "active_session_store_prefs" will be the filename for the DataStore file.
private val Context.sessionPrefsDataStore: DataStore<Preferences> by preferencesDataStore(name = "active_session_store_prefs")

class ActiveSessionStore(private val context: Context) {

    // Configure Json instance as needed (e.g., ignoreUnknownKeys for future-proofing)
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false // Set to true for debugging if you want human-readable JSON
    }

    // Define a key for storing the session JSON string in DataStore
    private object PreferencesKeys {
        val ACTIVE_SESSION_JSON = stringPreferencesKey("active_session_json_string")
    }

    /**
     * Saves the provided domain Session object to DataStore by converting it to
     * SessionPersistence and then to a JSON string.
     * This will overwrite any existing session.
     */
    suspend fun saveActiveSession(session: Session) { // Takes your domain Session
        try {
            val sessionPersistence = session.toPersistence() // Map to persistence model
            val sessionJsonString = json.encodeToString(sessionPersistence)
            context.sessionPrefsDataStore.edit { preferences ->
                preferences[PreferencesKeys.ACTIVE_SESSION_JSON] = sessionJsonString
            }
            Log.d("ActiveSessionStore", "Session saved successfully.")
        } catch (e: Exception) {
            Log.e("ActiveSessionStore", "Error saving session to DataStore", e)
            // Optionally rethrow or handle more gracefully (e.g., show user feedback)
        }
    }

    /**
     * Reads the active session from DataStore as a Flow.
     * Emits null if no session is stored or if there's an error in deserialization.
     */
    val activeSessionFlow: Flow<Session?> = context.sessionPrefsDataStore.data
        .catch { exception ->
            // Handle I/O errors when reading from DataStore
            Log.e("ActiveSessionStore", "Error reading from DataStore", exception)
            if (exception is IOException) {
                emit(emptyPreferences()) // Emit empty preferences to recover, results in null session
            } else {
                throw exception // Rethrow other critical exceptions
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.ACTIVE_SESSION_JSON]?.let { jsonString ->
                try {
                    val sessionPersistence = json.decodeFromString<SessionPersistence>(jsonString)
                    sessionPersistence.toDomain() // Map back to your domain Session
                } catch (e: Exception) {
                    Log.e("ActiveSessionStore", "Error deserializing session from JSON", e)
                    null // Return null if JSON is corrupt or structure mismatch
                }
            }
        }

    /**
     * Clears the currently stored active session from DataStore.
     */
    suspend fun clearActiveSession() {
        try {
            context.sessionPrefsDataStore.edit { preferences ->
                preferences.remove(PreferencesKeys.ACTIVE_SESSION_JSON)
            }
            Log.d("ActiveSessionStore", "Active session cleared successfully.")
        } catch (e: Exception) {
            Log.e("ActiveSessionStore", "Error clearing session from DataStore", e)
        }
    }
}