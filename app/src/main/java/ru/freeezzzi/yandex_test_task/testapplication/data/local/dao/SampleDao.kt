package ru.freeezzzi.yandex_test_task.testapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import ru.freeezzzi.yandex_test_task.testapplication.data.local.entities.SampleEntity

@Dao
interface SampleDao {
    @Query("SELECT * FROM samples")
    fun getAll() : List<SampleEntity>
}