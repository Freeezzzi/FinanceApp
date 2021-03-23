package ru.freeezzzi.yandex_test_task.testapplication.domain.repositories

import ru.freeezzzi.yandex_test_task.testapplication.domain.OperationResult
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.CompanyProfile
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.Quote

interface CompaniesRepository {

    suspend fun getCompanyProfile(symbol: String): OperationResult<CompanyProfile, String?>

    suspend fun getCompanyQuote(symbol: String): OperationResult<Quote, String?>

    suspend fun getCompaniesTop500(symbol: String): OperationResult<List<String>, String?>

    suspend fun symbolLookup(symbol: String): OperationResult<List<String>, String?>
}
