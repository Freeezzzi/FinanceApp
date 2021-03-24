package ru.freeezzzi.yandex_test_task.testapplication.data.network

import retrofit2.http.GET
import retrofit2.http.Query
import ru.freeezzzi.yandex_test_task.testapplication.BuildConfig
import ru.freeezzzi.yandex_test_task.testapplication.data.network.models.*

interface FinnhubApi {

    @GET("stock/profile2")
    suspend fun getCompanyProfile(
        @Query(value = "symbol") symbol: String,
        @Query(value = "token") token: String = BuildConfig.API_KEY
    ): CompanyProfileDTO

    @GET("quote")
    suspend fun getCompanyQuote(
        @Query(value = "symbol") symbol: String,
        @Query(value = "token") token: String = BuildConfig.API_KEY
    ): QuoteDTO

    @GET("index/constituents")
    suspend fun getCompaniesTop500(
        @Query(value = "symbol", encoded = true) symbol: String,
        @Query(value = "token") token: String = BuildConfig.API_KEY
    ): IndicesConstituentsDTO

    @GET("search")
    suspend fun symbolLookup(
        @Query(value = "q", encoded = true) symbol: String,
        @Query(value = "token") token: String = BuildConfig.API_KEY
    ): SymbolLookupDTO

    @GET("stock/candle")
    suspend fun getStockCandle(
        @Query(value = "symbol", encoded = true) symbol: String,
        @Query(value = "resolution") resolution: String,
        @Query(value = "from") from: Long,
        @Query(value = "to") to: Long,
        @Query(value = "token") token: String = BuildConfig.API_KEY
    ): StockCandleDTO
}
