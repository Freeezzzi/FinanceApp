package ru.freeezzzi.yandex_test_task.testapplication.data.local.dao

import androidx.room.*
import ru.freeezzzi.yandex_test_task.testapplication.data.local.entities.CompanyProfileEntity
import ru.freeezzzi.yandex_test_task.testapplication.data.local.entities.RecentQueryEntity

// TODO сделать поиск по имени компании
@Dao
interface CompanyProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recentQueryEntity: RecentQueryEntity): Unit

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(companyProfileEntity: CompanyProfileEntity): Unit

    @Delete
    suspend fun delete(companyProfileEntity: CompanyProfileEntity): Unit

    @Update()
    suspend fun update(companyProfileEntity: CompanyProfileEntity): Unit

    @Query("SELECT * FROM Favorite_companies")
    suspend fun getFavoriteCompanies(): List<CompanyProfileEntity>

    @Query("SELECT * FROM Queries")
    suspend fun getQueries(): List<RecentQueryEntity>

    /**
     * @param symbol для поиска частичного совпадения(например символ внутри тикера компании, а не весь тикер) при передаче аргумента передать %symbol%
     */
    @Query("SELECT * FROM Favorite_companies WHERE ticker LIKE :symbol OR name LIKE :symbol")
    suspend fun findInFavouritesCompanies(symbol: String): List<CompanyProfileEntity>

    @Query("SELECT EXISTS (SELECT 1 FROM FAVORITE_COMPANIES WHERE ticker = :symbol)")
    suspend fun isCompanyInFavorite(symbol: String): Boolean
}
