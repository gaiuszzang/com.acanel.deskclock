package com.acanel.deskclock.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.acanel.deskclock.repo.ClockRepository
import com.acanel.deskclock.repo.ClockSettingRepository
import com.acanel.deskclock.repo.ImageRepository
import com.acanel.deskclock.repo.StringRepository
import com.acanel.deskclock.repo.db.DeskClockDao
import com.acanel.deskclock.repo.db.DeskClockDatabase
import com.acanel.deskclock.repo.fb.DeskClockFbApi
import com.acanel.deskclock.repo.fb.DeskClockFbService
import com.acanel.deskclock.repo.impl.AndroidClockRepository
import com.acanel.deskclock.repo.impl.AndroidClockSettingRepository
import com.acanel.deskclock.repo.impl.AndroidStringRepository
import com.acanel.deskclock.repo.impl.FbImageRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/*
 * Interface DI with Singleton Components
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class InterfaceDIModule {
    @Binds
    @Singleton
    abstract fun bindClockRepository(repo: AndroidClockRepository): ClockRepository

    @Binds
    @Singleton
    abstract fun bindClockSettingRepository(repo: AndroidClockSettingRepository): ClockSettingRepository

    @Binds
    @Singleton
    abstract fun bindImageRepository(repo: FbImageRepository): ImageRepository

    @Binds
    @Singleton
    abstract fun bindStringRepository(repo: AndroidStringRepository): StringRepository
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
    @Singleton
    @ClockSettingDataStore
    fun provideClockSettingDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.clockSettingDataStore
    }

    @Provides
    @Singleton
    @UnsplashSettingDataStore
    fun provideUnsplashSettingDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.unsplashSettingDataStore
    }
}


/*
 * Room DB DI with Singleton Components
 */
@Module
@InstallIn(SingletonComponent::class)
class RoomDBDIModule {
    @Provides
    @Singleton
    fun provideDeskClockDatabase(@ApplicationContext context: Context): DeskClockDatabase {
        return Room.databaseBuilder(context, DeskClockDatabase::class.java, "DeskClock.db")
            .build()
    }

    @Provides
    @Singleton
    fun provideDeskClockDao(db: DeskClockDatabase): DeskClockDao {
        return db.getDao()
    }
}

/*
 * Retrofit DI with Singleton Components
 */
@Module
@InstallIn(SingletonComponent::class)
class RetrofitDIModule {
    @Provides
    @Singleton
    fun provideDeskClockFbService(): DeskClockFbApi = DeskClockFbService.service
}