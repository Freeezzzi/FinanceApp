package ru.freeezzzi.yandex_test_task.testapplication.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject

class PrefsStorage @Inject constructor(
    context: Context
) {
    companion object {
        private const val USERNAME_KEY = "username"
        private const val NAME_KEY = "name"
        private const val TOKEN_KEY = "token"
        private const val IS_MENTOR_KEY = "IsMentor"
    }
}
