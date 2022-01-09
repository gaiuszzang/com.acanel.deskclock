package com.acanel.deskclock.repo.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.acanel.deskclock.di.UnsplashSettingDataStore
import com.acanel.deskclock.entity.ErrorType
import com.acanel.deskclock.entity.RepoResult
import com.acanel.deskclock.entity.UnsplashImageVO
import com.acanel.deskclock.repo.ImageRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.lang.Exception
import javax.inject.Inject

class FbImageRepository @Inject constructor(
    @UnsplashSettingDataStore private val dataStore: DataStore<Preferences>
): BaseDataStoreRepository(dataStore), ImageRepository {
    private val randomPhoto by lazy { stringPreferencesKey("randomPhoto") }

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://us-central1-acanel-deskclock.cloudfunctions.net")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val service = retrofit.create(FbApi::class.java)

    override fun getBackgroundImageFlow(): Flow<RepoResult<UnsplashImageVO>> {
        return flow {
            val response = service.backgroundImageRandom()
            val unsplashImageVO = response.body()
            if (response.isSuccessful && unsplashImageVO != null) {
                setPreference(randomPhoto, Gson().toJson(unsplashImageVO))
                emit(RepoResult(data = unsplashImageVO))
            } else {

                val randomPhotoJson = getPreference(randomPhoto, "")
                try {
                    val cachedUnsplashImageVO =
                        Gson().fromJson(randomPhotoJson, UnsplashImageVO::class.java)
                    if (cachedUnsplashImageVO != null) {
                        emit(RepoResult(data = cachedUnsplashImageVO))
                    } else {
                        emit(RepoResult(error = ErrorType.NETWORK_ERROR))
                    }
                } catch (e: Exception) {
                    emit(RepoResult(error = ErrorType.NETWORK_ERROR))
                }
            }
        }
    }

    interface FbApi {
        @GET("/api/backgroundImage/random")
        suspend fun backgroundImageRandom() : Response<UnsplashImageVO>
    }
}
