package ru.freeezzzi.yandex_test_task.testapplication.domain.repositories

import ru.freeezzzi.yandex_test_task.testapplication.domain.OperationResult
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.*

interface CompaniesRepository {

    suspend fun getCompanyProfile(symbol: String): OperationResult<CompanyProfile, String?>

    suspend fun getCompanyQuote(symbol: String): OperationResult<Quote, String?>

    suspend fun getCompaniesTop500(symbol: String): OperationResult<List<String>, String?>

    suspend fun symbolLookup(symbol: String): OperationResult<List<String>, String?>

    suspend fun getStockCandle(symbol: String, resolution: String, from: Long, to: Long): OperationResult<StockCandle, String?>

    suspend fun getCompanyNews(symbol: String, from: String, to: String): OperationResult<List<News>, String?>

    suspend fun getCompanyPeers(symbol: String): OperationResult<List<String>, String?>

    suspend fun getRecommendationTrends(symbol: String): OperationResult<List<RecommendationTrend>, String?>
}
