package com.javaindoku.yotaniniaga.service.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.javaindoku.yotaniniaga.model.entity.DummyEntity
import com.javaindoku.yotaniniaga.service.database.dao.DummyEntityDao

@Database(
    entities =[DummyEntity::class], version = 2
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dummyEntityDao(): DummyEntityDao
}