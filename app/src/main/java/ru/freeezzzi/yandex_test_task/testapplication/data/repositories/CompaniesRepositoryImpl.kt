package ru.freeezzzi.yandex_test_task.testapplication.data.repositories

import ru.freeezzzi.yandex_test_task.testapplication.data.network.FinnhubApi
import ru.freeezzzi.yandex_test_task.testapplication.domain.OperationResult
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.CompanyProfile
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.Quote
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
}
