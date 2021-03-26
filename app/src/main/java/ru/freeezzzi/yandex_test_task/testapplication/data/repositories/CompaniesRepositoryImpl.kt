package ru.freeezzzi.yandex_test_task.testapplication.data.repositories

import ru.freeezzzi.yandex_test_task.testapplication.data.network.FinnhubApi
import ru.freeezzzi.yandex_test_task.testapplication.domain.OperationResult
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.*
import ru.freeezzzi.yandex_test_task.testapplication.domain.repositories.CompaniesRepository
import javax.inject.Inject

class CompaniesRepositoryImpl @Inject constructor(
    private val finnhabApi: FinnhubApi
) : CompaniesRepository {

    override suspend fun getCompanyProfile(symbol: String): OperationResult<CompanyProfile, String?> =
        try {
            val companyProfile = finnhabApi.getCompanyProfile(symbol).toCompanyProfile()
            OperationResult.Success(companyProfile)
        } catch (e: Throwable) {
            OperationResult.Error(e.message)
        }

    override suspend fun getCompanyQuote(symbol: String): OperationResult<Quote, String?> =
            try {
                val quote = finnhabApi.getCompanyQuote(symbol).toQuote()
                OperationResult.Success(quote)
            } catch (e: Throwable) {
                OperationResult.Error(e.message)
            }

    override suspend fun getCompaniesTop500(symbol: String): OperationResult<List<String>, String?> =
            try {
                val list = finnhabApi.getCompaniesTop500(symbol).constituents ?: emptyList()
                OperationResult.Success(list)
            } catch (e: Throwable) {
                OperationResult.Error(e.message)
            }

    override suspend fun symbolLookup(symbol: String): OperationResult<List<String>, String?> =
        try {
            val list = (finnhabApi.symbolLookup(symbol).result ?: emptyList())
            // Проверим что такой компании еще нет. Если описания совпадают то это разные подразделенрия одной компании, а акции у них одни => можем взять только одну
            val descriptionsList: MutableList<String> = mutableListOf()
            val tickersList: MutableList<String> = mutableListOf()
            list.forEach {
                if (!descriptionsList.contains(it?.description)) {
                    tickersList.add(it?.symbol ?: "")
                    descriptionsList.add(it?.description ?: "")
                }
            }
            OperationResult.Success(tickersList)
        } catch (e: Throwable) {
            OperationResult.Error(e.message)
        }

    override suspend fun getStockCandle(
        symbol: String,
        resolution: String,
        from: Long,
        to: Long
    ): OperationResult<StockCandle, String?> =
        try {
            val stockCandle = finnhabApi.getStockCandle(symbol, resolution, from, to).toStockCandle()
            OperationResult.Success(stockCandle)
        } catch (e: Throwable) {
            OperationResult.Error(e.message)
        }

    override suspend fun getCompanyNews(
        symbol: String,
        from: String, // YYYY-MM-DD
        to: String // YYYY-MM-DD
    ): OperationResult<List<News>, String?> =
        try {
            val companyNews = finnhabApi.getCompanyNews(symbol, from, to).map {
                it.toNews()
            }
            OperationResult.Success(companyNews)
        } catch (e: Throwable) {
            OperationResult.Error(e.message)
        }

    override suspend fun getCompanyPeers(symbol: String): OperationResult<List<String>, String?> =
        try {
            val tickersList = finnhabApi.getCompanyPeers(symbol)
            OperationResult.Success(tickersList)
        } catch (e: Throwable) {
            OperationResult.Error(e.message)
        }

    override suspend fun getRecommendationTrends(symbol: String): OperationResult<List<RecommendationTrend>, String?> =
        try {
            val trends = finnhabApi.getRecommendationTrends(symbol).map {
                it.toRecomendationTrend()
            }
            OperationResult.Success(trends)
        } catch (e: Throwable) {
            OperationResult.Error(e.message)
        }
}
