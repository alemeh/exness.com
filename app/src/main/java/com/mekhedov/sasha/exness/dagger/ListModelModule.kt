package com.mekhedov.sasha.exness.dagger

import com.mekhedov.sasha.exness.common.RxBus
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class ListModelModule    {
    @Provides
    @Singleton
    fun provideOkHttpClient() : OkHttpClient {
        val logging : HttpLoggingInterceptor = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return OkHttpClient.Builder()
                .pingInterval(30, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .readTimeout(0, TimeUnit.MILLISECONDS).build()
    }

    @Provides
    @Singleton
    fun provideRxBus() : RxBus = RxBus

}
