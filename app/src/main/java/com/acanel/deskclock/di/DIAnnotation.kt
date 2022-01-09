package com.acanel.deskclock.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ClockSettingDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UnsplashSettingDataStore