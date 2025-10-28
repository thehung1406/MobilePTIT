package com.example.btl.di

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Cung cấp một đối tượng Retrofit duy nhất trong toàn ứng dụng
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        // Bạn cần thay thế "https://api.example.com/" bằng Base URL của API thực tế
        return Retrofit.Builder()
            .baseUrl("https://api.example.com/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
    }

    // Ví dụ: Cung cấp một ApiService (bạn cần tự tạo interface này)
    /*
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): YourApiService {
        return retrofit.create(YourApiService::class.java)
    }
    */
}
