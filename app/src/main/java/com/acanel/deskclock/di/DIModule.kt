package com.acanel.deskclock.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.acanel.deskclock.repo.ClockRepository
import com.acanel.deskclock.repo.ClockSettingRepository
import com.acanel.deskclock.repo.ImageRepository
import com.acanel.deskclock.repo.impl.AndroidClockRepository
import com.acanel.deskclock.repo.impl.DataStoreClockSettingRepository
import com.acanel.deskclock.repo.impl.FbImageRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

/*
 * Interface DI with Singleton Components
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class InterfaceDIModule {
    @Binds
    abstract fun bindClockRepository(repo: AndroidClockRepository): ClockRepository

    @Binds
    abstract fun bindClockSettingRepository(repo: DataStoreClockSettingRepository): ClockSettingRepository

    @Binds
    abstract fun bindImageRepository(repo: FbImageRepository): ImageRepository
}


/*
 * DataStore DI with Singleton Components
 */
private val Context.clockSettingDataStore: DataStore<Preferences> by preferencesDataStore(name = "clockSetting")
private val Context.unsplashSettingDataStore: DataStore<Preferences> by preferencesDataStore(name = "unsplashSetting")

@Module
@InstallIn(SingletonComponent::class)
class DataStoreDIModule {
    @Provides
    @ClockSettingDataStore
    fun provideClockSettingDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.clockSettingDataStore
    }

    @Provides
    @UnsplashSettingDataStore
    fun provideUnsplashSettingDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.unsplashSettingDataStore
    }
}
