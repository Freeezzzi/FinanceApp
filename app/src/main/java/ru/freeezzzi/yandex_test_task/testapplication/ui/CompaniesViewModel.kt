package ru.freeezzzi.yandex_test_task.testapplication.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.freeezzzi.yandex_test_task.testapplication.data.local.FavoriteCompaniesDatabase
import ru.freeezzzi.yandex_test_task.testapplication.domain.OperationResult
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.CompanyProfile
import ru.freeezzzi.yandex_test_task.testapplication.domain.models.toCompanyProfileEntity
import ru.freeezzzi.yandex_test_task.testapplication.domain.repositories.CompaniesRepository

/**
 * Позволяет загружать список компаний. Тикеры компаний нужно класть в mutableTickersList.
 */
abstract class CompaniesViewModel constructor(
    private val companiesRepository: CompaniesRepository,
    private val database: FavoriteCompaniesDatabase
) : ViewModel() {
    /**
     * Здесь используется singleLiveEvent т.к. при возврате к фрагменту к livedata заново привязываются наблюдатели и получают обновление при привязке
     * Это создает лишние запросы к api, которые, ввиду огранчиений, хотелось бы избежать
     */
    protected var mutableTickersList: SingleLiveEvent<ViewState<List<String>, String?>> = SingleLiveEvent()
    val tickersList: LiveData<ViewState<List<String>, String?>> get() = mutableTickersList
    protected val mutableCompanies = MutableLiveData<ViewState<MutableList<CompanyProfile>, String?>>()
    val companies: LiveData<ViewState<MutableList<CompanyProfile>, String?>> get() = mutableCompanies

    protected var tickersCount = 0 // сколько уже загрузили(некоторые могут быть не валдины и не отображаться)

    protected var numberOfCompanies = 0 // сколько компаний получили с сервера

    open fun addToFavorites(companyProfile: CompanyProfile) {
        viewModelScope.launch {
            when (companyProfile.isFavorite) {
                true -> { // Нужно удалить
                    database.companyProfileDao().delete(companyProfile.toCompanyProfileEntity())
                    companyProfile.isFavorite = false
                }
                false -> {
                    companyProfile.isFavorite = true
                    database.companyProfileDao().insert(companyProfile.toCompanyProfileEntity())
                }
            }
        }
    }

    fun getCompanies(howManyCompanies: Int) {
        var lastCompanyToDownload = tickersCount + howManyCompanies
        if (numberOfCompanies == 0) mutableCompanies.value = ViewState.success(mutableListOf()) // Если найдено 0 компаний, то удалим старые данные
        if (tickersCount == numberOfCompanies) return // если загрузили все компании
        if (lastCompanyToDownload > numberOfCompanies) { // если хотим загрузить больше компаний чем осталось
            lastCompanyToDownload = numberOfCompanies
        }
        if (mutableCompanies.value is ViewState.Loading && tickersCount != 0) return

        viewModelScope.launch {
            // Проверим есть ли уже в сптске данные
            var companiesList: MutableList<CompanyProfile> = mutableListOf()
            when (mutableCompanies.value) {
                is ViewState.Success -> companiesList = (mutableCompanies.value as ViewState.Success<MutableList<CompanyProfile>>).result
                is ViewState.Error -> companiesList = (mutableCompanies.value as ViewState.Error<MutableList<CompanyProfile>, String?>).oldvalue
            }
            // Здесь приходится копировать лист, т.к. если ссылка останется той же то submitList адаптера посчитает что они одинаковые и не обновит RecyclerView
            companiesList = companiesList.toMutableList()

            var state: ViewState<MutableList<CompanyProfile>, String?> = ViewState.loading() // Сюда будем записывать временные значения, пока не загрузим все значения
            mutableCompanies.value = ViewState.loading() // Сообщаем что идет загрузка
            when (tickersList.value) {
                is ViewState.Success -> {
                    // Для каждого запросим компанию и для нее quote
                    for (ticker in
                    (tickersList.value as ViewState.Success<List<String>>).result.subList(tickersCount, lastCompanyToDownload)) {
                        when (val companiesResult = companiesRepository.getCompanyProfile(ticker)) {
                            is OperationResult.Success -> { // Если удачно, то добавляем компанию в
                                val companyProfile = companiesResult.data
                                if (companyProfile.ticker == null) { // Если с сервера пришел невалидный ответ, то пропускаем эту компанию
                                    tickersCount++
                                    state = ViewState.success(companiesList) // если у нас все компании не валдины, то нужно сообщить что мы все успешно обработали
                                    continue
                                }

                                // getQuote(companyProfile)
                                when (val quoteResult = companiesRepository.getCompanyQuote(companyProfile.ticker)) {
                                    is OperationResult.Success -> companyProfile.quote = quoteResult.data
                                    is OperationResult.Error -> companyProfile.quote = null
                                }

                                if (database.companyProfileDao().isCompanyInFavorite(companyProfile.ticker)) {
                                    companyProfile.isFavorite = true
                                    database.companyProfileDao().update(companyProfile.toCompanyProfileEntity())
                                }

                                companiesList.add(companyProfile)
                                state = ViewState.success(companiesList)
                                tickersCount++
                            }
                            is OperationResult.Error -> state = ViewState.Error(companiesList, companiesResult.data)
                        }
                    }
                    mutableCompanies.value = state
                }
                is ViewState.Error -> mutableCompanies.value = ViewState.error(companiesList, (tickersList.value as ViewState.Error<List<String>, String?>).result)
                is ViewState.Loading -> mutableCompanies.value = ViewState.error(companiesList, "Tickers loading.Try again!")
            }
        }
    }

    open fun clearCompaniesList() {
        tickersCount = 0
        mutableCompanies.value = ViewState.loading()
    }
}