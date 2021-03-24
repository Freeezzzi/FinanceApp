package ru.freeezzzi.yandex_test_task.testapplication.data.local.dao

import androidx.room.*
import ru.freeezzzi.yandex_test_task.testapplication.data.local.entities.CompanyProfileEntity
import ru.freeezzzi.yandex_test_task.testapplication.data.local.entities.RecentQueryEntity

@Dao
interface CompanyProfileDao {
    // TODO добавить решулярное выражение в поиск по локальной бд
    //TODO добавить поиск не толкьо по тикеру
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recentQueryEntity: RecentQueryEntity): Unit

    @Insert
    suspend fun insert(companyProfileEntity: CompanyProfileEntity): Unit

    @Delete
    suspend fun delete(companyProfileEntity: CompanyProfileEntity): Unit

    @Update()
    suspend fun update(companyProfileEntity: CompanyProfileEntity): Unit

    @Query("SELECT * FROM Favorite_companies")
    suspend fun getFavoriteCompanies(): List<CompanyProfileEntity>

    @Query("SELECT * FROM Queries")
    suspend fun getQueries(): List<RecentQueryEntity>

    @Query("SELECT * FROM Favorite_companies WHERE ticker LIKE :symbol")
    suspend fun findInFavouritesCompanies(symbol: String): List<CompanyProfileEntity>

    @Query("SELECT EXISTS (SELECT 1 FROM FAVORITE_COMPANIES WHERE ticker = :symbol)")
    suspend fun isCompanyInFavorite(symbol: String): Boolean
}
