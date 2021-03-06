package ru.freeezzzi.yandex_test_task.testapplication.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Queries")
data class RecentQueryEntity(
        @PrimaryKey @ColumnInfo(name = "Query") var query: String = "",
)
