package com.it2161.s243168t.movieviewer.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.favouritesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "favourites_preferences"
)

class FavouritesDataStore(
    private val context: Context
) {
    companion object {
        private val FAVOURITE_IDS_KEY = stringPreferencesKey("favourite_ids")
    }

    fun getFavouriteIds(): Flow<Set<String>> {
        return context.favouritesDataStore.data.map { preferences ->
            val idsString = preferences[FAVOURITE_IDS_KEY] ?: ""
            if (idsString.isBlank()) {
                emptySet()
            } else {
                idsString.split(",").toSet()
            }
        }
    }

    suspend fun toggleFavourite(movieId: Int) {
        context.favouritesDataStore.edit { preferences ->
            val currentIdsString = preferences[FAVOURITE_IDS_KEY] ?: ""
            val currentIds = if (currentIdsString.isBlank()) {
                mutableSetOf()
            } else {
                currentIdsString.split(",").toMutableSet()
            }

            val movieIdString = movieId.toString()
            if (currentIds.contains(movieIdString)) {
                currentIds.remove(movieIdString)
            } else {
                currentIds.add(movieIdString)
            }

            preferences[FAVOURITE_IDS_KEY] = currentIds.joinToString(",")
        }
    }
}
