package com.arya.submission3.data.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arya.submission3.data.remote.response.Story

@Dao
interface StoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(story: Story)

    @Query("SELECT * FROM story")
    fun getAll(): PagingSource<Int, Story>

    @Query("DELETE FROM story")
    suspend fun deleteAll()

}