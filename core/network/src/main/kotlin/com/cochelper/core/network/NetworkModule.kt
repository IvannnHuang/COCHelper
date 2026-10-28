package com.cochelper.core.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun json(): Json = Json { ignoreUnknownKeys = true }

    @Provides
    @Singleton
    fun okHttpClient(): OkHttpClient = OkHttpClient()
}
