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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class FbImageRepository @Inject constructor(
    @UnsplashSettingDataStore private val dataStore: DataStore<Preferences>,
    private val fb: DeskClockFbApi
): BaseDataStoreRepository(dataStore), ImageRepository {
    private val randomPhoto by lazy { stringPreferencesKey("randomPhoto") }

    override suspend fun getBackgroundImage(slug: String): RepoResult<UnsplashImageVO> {
        val response: Response<UnsplashImageVO>? =
            withContext(Dispatchers.IO) {
                noSuspendException {
                    fb.getBackgroundImage(slug)
                }
            }
        return if (response != null && response.isSuccessful && response.body() != null) {
            val unsplashImageVO = response.body()
            setPreference(randomPhoto, Gson().toJson(unsplashImageVO))
            RepoResult(data = unsplashImageVO)
        } else {
            val randomPhotoJson = getPreference(randomPhoto, "")
            val cachedUnsplashImageVO: UnsplashImageVO? = noException {
                Gson().fromJson(randomPhotoJson, UnsplashImageVO::class.java)
            }
            if (cachedUnsplashImageVO != null) {
                RepoResult(data = cachedUnsplashImageVO)
            } else {
                RepoResult(error = ErrorType.NETWORK_ERROR)
            }
        }
    }
}
