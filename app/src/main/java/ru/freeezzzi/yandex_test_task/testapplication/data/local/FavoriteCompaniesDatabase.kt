package ru.freeezzzi.yandex_test_task.testapplication.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.freeezzzi.yandex_test_task.testapplication.data.local.dao.CompanyProfileDao
import ru.freeezzzi.yandex_test_task.testapplication.data.local.entities.CompanyProfileEntity
import ru.freeezzzi.yandex_test_task.testapplication.data.local.entities.RecentQueryEntity

@Database(entities = [CompanyProfileEntity::class, RecentQueryEntity::class], version = 5, exportSchema = false)
abstract class FavoriteCompaniesDatabase : RoomDatabase() {

    abstract fun companyProfileDao(): CompanyProfileDao

    companion object {
        private const val DATABASE_NAME = "Favorite_companies_database"

        @Volatile
        private var instance: FavoriteCompaniesDatabase? = null

        fun getInstance(context: Context): FavoriteCompaniesDatabase =
            instance ?: synchronized(this) { instance ?: build(context) }

        private fun build(context: Context): FavoriteCompaniesDatabase =
            Room.databaseBuilder(context, FavoriteCompaniesDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
    }
}