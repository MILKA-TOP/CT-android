package example.app_response_base_hw8.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import example.app_response_base_hw8.DATABASE_NAME
import example.app_response_base_hw8.ResponseData


@Dao
interface ResponseDao {

    @Query(value = "SELECT * FROM $DATABASE_NAME")
    suspend fun getAll(): List<ResponseData>

    @Insert
    suspend fun insertResponse(response: ResponseData)

    @Insert
    suspend fun insertAll(responses: List<ResponseData>)

    @Delete
    suspend fun deleteResponse(response: ResponseData)

    @Query("DELETE FROM $DATABASE_NAME")
    suspend fun clearDb()

    @Query("SELECT EXISTS (SELECT 1 FROM $DATABASE_NAME WHERE postId = :id)")
    fun getById(id: Int): Boolean
}