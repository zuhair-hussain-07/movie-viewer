package com.it2161.s243168t.movieviewer.di

import android.content.Context
import com.it2161.s243168t.movieviewer.data.local.datastore.FavouritesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideFavouritesDataStore(
        @ApplicationContext context: Context
    ): FavouritesDataStore {
        return FavouritesDataStore(context)
    }
}
