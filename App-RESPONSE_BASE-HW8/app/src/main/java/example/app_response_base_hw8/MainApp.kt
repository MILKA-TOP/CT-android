package example.app_response_base_hw8

import android.app.Application
import example.app_response_base_hw8.responses_api.ResponseService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import example.app_response_base_hw8.database.AppDatabase

import androidx.room.Room




class MainApp : Application() {

    lateinit var responseService: ResponseService
    lateinit var dataBase: AppDatabase

    override fun onCreate() {
        super.onCreate()
        instance = this

        retrofitReconnection()

        dataBase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, DATABASE_NAME
        ).build()

    }

    fun retrofitReconnection() {
        val mRetrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        responseService = mRetrofit.create()
    }

    companion object {
        lateinit var instance: MainApp
            private set
    }


}