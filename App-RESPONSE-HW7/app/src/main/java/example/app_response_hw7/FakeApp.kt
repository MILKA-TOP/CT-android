package example.app_response_hw7

import android.app.Application
import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

class FakeApp : Application() {

    lateinit var responseService: ResponseService

    override fun onCreate() {
        super.onCreate()
        instance = this
        val mRetrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        responseService = mRetrofit.create()
    }

    companion object {
        lateinit var instance: FakeApp
            private set
    }


}