package com.javaindoku.yotaniniaga.di.module

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.javaindoku.yotaniniaga.service.database.AppDatabase
import dagger.Module
import dagger.Provides
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideSqlCipher(): SupportFactory {
        val passphrase: ByteArray = SQLiteDatabase.getBytes(DATABASE_PASSWORD.toCharArray())
        return SupportFactory(passphrase)
    }

    @Singleton
    @Provides
    fun provideAppDatabase(context: Context, supportFactory: SupportFactory): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).allowMainThreadQueries().fallbackToDestructiveMigration().addCallback(object: RoomDatabase.Callback() {
        }).openHelperFactory(supportFactory)
            .build()

    @Singleton
    @Provides
    fun provideDummyEntityDao(appDatabase: AppDatabase) = appDatabase.dummyEntityDao()

    companion object {
        private const val DATABASE_NAME = "yotaniNiagaDb"
        private const val DATABASE_PASSWORD = "yotaniNiagaPass"
    }
}