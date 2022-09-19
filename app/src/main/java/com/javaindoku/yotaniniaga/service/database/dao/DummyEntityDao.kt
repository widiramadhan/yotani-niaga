package com.javaindoku.yotaniniaga.service.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.javaindoku.yotaniniaga.model.entity.DummyEntity

@Dao
interface DummyEntityDao {
    @Query("SELECT * FROM DummyEntity")
    fun getAll(): LiveData<List<DummyEntity>>

    @Query("SELECT * FROM DummyEntity WHERE id=:id")
    fun getById(id: String): LiveData<DummyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg items: DummyEntity?)

    @Update
    suspend fun update(vararg items: DummyEntity?)

    @Delete
    suspend fun delete(item: DummyEntity?)
}