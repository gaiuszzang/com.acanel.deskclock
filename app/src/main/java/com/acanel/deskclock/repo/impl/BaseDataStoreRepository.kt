package com.acanel.deskclock.repo.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

open class BaseDataStoreRepository(
    private val dataStore: DataStore<Preferences>
) {
    protected fun <T> getPreferenceFlow(key: Preferences.Key<T>, defaultValue: T): Flow<T> =
        flow {
            var currentValue: T? = null
            dataStore.data.collect { preferences ->
                val newValue = preferences[key] ?: defaultValue
                if (currentValue != newValue) {
                    currentValue = newValue
                    emit(newValue)
                }
            }
        }

    protected suspend fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T): T {
        return dataStore.data.map { preferences -> preferences[key] ?: defaultValue }.first()
    }

    protected suspend fun <T> setPreference(key: Preferences.Key<T>, value: T) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }
}