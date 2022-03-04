package example.app_response_base_hw8.database

import androidx.room.RoomDatabase

import androidx.room.Database
import example.app_response_base_hw8.ResponseData


@Database(entities = [ResponseData::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun responseDao(): ResponseDao?
}
