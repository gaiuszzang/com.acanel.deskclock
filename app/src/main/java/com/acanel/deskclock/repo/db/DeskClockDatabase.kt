package com.acanel.deskclock.repo.db

import androidx.room.*
import com.acanel.deskclock.entity.UnsplashTopicVO

@Database(entities = [UnsplashTopicVO::class], version = 1)
abstract class DeskClockDatabase: RoomDatabase() {
    abstract fun getDao(): DeskClockDao
}


@Dao
interface DeskClockDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUnsplashTopicList(list: List<UnsplashTopicVO>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUnsplashTopic(item: UnsplashTopicVO)
    @Query("DELETE FROM UnsplashTopic")
    suspend fun removeUnsplashTopicListAll()
    @Query("SELECT * FROM UnsplashTopic")
    suspend fun getUnsplashTopicList(): List<UnsplashTopicVO>
}