package ru.freeezzzi.yandex_test_task.testapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ru.freeezzzi.yandex_test_task.testapplication.data.local.entities.CompanyProfileEntity
@Dao
interface CompanyProfileDao {
    @Insert
    suspend fun insert(companyProfileEntity: CompanyProfileEntity): Unit

    @Delete
    suspend fun delete(companyProfileEntity: CompanyProfileEntity): Unit

    @Query("SELECT * FROM Favorite_companies")
    suspend fun getFavoriteCompanies(): List<CompanyProfileEntity>
}
