package fr.esgi.taskflow.data.network

import android.util.Log
import fr.esgi.taskflow.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    private val httpClient = OkHttpClient.Builder().apply {
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor { message ->
                message.chunked(3_000).forEach { part ->
                    Log.d("TaskFlowHTTP", part)
                }
            }.apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
            addInterceptor(logging)
        }
    }.build()

    fun createTaskApiService(): TaskApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create()) // Map JSON -> Data class
        .build()
        .create(TaskApiService::class.java)
}
