package com.example.aifeel.data.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {

        private const val BASE_URL_API_1 = "http://34.101.139.121:4000/"
        private const val BASE_URL_API_2 = "http://34.101.41.219:3000/"

        private fun getOkHttpClient(): OkHttpClient {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
        }

        fun getApiService(): ApiService {
            val client = getOkHttpClient()
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL_API_1)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }

        fun getApiServiceML(): ApiService {
            val client = getOkHttpClient()
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL_API_2)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}
