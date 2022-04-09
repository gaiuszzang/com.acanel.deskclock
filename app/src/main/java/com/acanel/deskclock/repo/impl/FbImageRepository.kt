package com.acanel.deskclock.repo.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.acanel.deskclock.di.UnsplashSettingDataStore
import com.acanel.deskclock.entity.ErrorType
import com.acanel.deskclock.entity.RepoResult
import com.acanel.deskclock.entity.UnsplashImageVO
import com.acanel.deskclock.repo.ImageRepository
import com.acanel.deskclock.repo.fb.DeskClockFbApi
import com.acanel.deskclock.utils.noException
import com.acanel.deskclock.utils.noSuspendException
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

class FbImageRepository @Inject constructor(
    @UnsplashSettingDataStore private val dataStore: DataStore<Preferences>,
    private val fb: DeskClockFbApi
): BaseDataStoreRepository(dataStore), ImageRepository {
    private val randomPhoto by lazy { stringPreferencesKey("randomPhoto") }

    override fun getBackgroundImageFlow(slug: String): Flow<RepoResult<UnsplashImageVO>> {
        return flow {
            val response: Response<UnsplashImageVO>? = noSuspendException { fb.getBackgroundImage(slug) }
            if (response != null && response.isSuccessful && response.body() != null) {
                val unsplashImageVO = response.body()
                setPreference(randomPhoto, Gson().toJson(unsplashImageVO))
                emit(RepoResult(data = unsplashImageVO))
            } else {
                val randomPhotoJson = getPreference(randomPhoto, "")
                val cachedUnsplashImageVO: UnsplashImageVO? = noException {
                    Gson().fromJson(randomPhotoJson, UnsplashImageVO::class.java)
                }
                if (cachedUnsplashImageVO != null) {
                    emit(RepoResult(data = cachedUnsplashImageVO))
                } else {
                    emit(RepoResult(error = ErrorType.NETWORK_ERROR))
                }
            }
        }
    }
}
