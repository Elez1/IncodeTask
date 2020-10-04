package com.scheduler.incodetask.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val BASE_URL = "https://www.json-generator.com/api/json/"

    fun provideRetrofit() = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()

    fun providePhotoService(retrofit: Retrofit): PhotoService = retrofit.create(PhotoService::class.java)
}