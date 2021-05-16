# FinanceApp
An app for viewing the company's stock price, price changes, and more information about the company. App using Finnhub open api to access information.

## Особенности работы для уменьшения количества запросов на сервер
* Пагинация - во всех списках компаний первоначально с сервера запрашивается 10 профилей компании, а при прокрутке дальше еще по 7 за раз.
* Профили компаний в favourites хранятся локально на устройстве и обновляются только когда такая компания появится в списке акций. Это так же седално для уменьшения количества запросов на сервер
* При открытии профиля компании ее профиль не запрашивается завново на сервер, а используется тот обьект на который пользователь нажал.
## Используемые библиотеки
* Kotlin coroutines
* Android Room
* Cicerone - для навигации по приложению
* Moshi
* Retrofit2 вместе Okhttp3
* Dagger2
* Picasso - для загрузки изоюражений
* MPAndroidChart - для создания графика
## Демонстрация
[<img src="https://img.youtube.com/vi/EM2nVT6rG3Y/maxresdefault.jpg" width="50%">](https://youtu.be/EM2nVT6rG3Y)
