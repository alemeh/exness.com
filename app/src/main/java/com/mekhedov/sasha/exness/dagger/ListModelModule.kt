package com.mekhedov.sasha.exness.dagger

import com.appunite.websocket.rx.RxWebSockets
import com.appunite.websocket.rx.`object`.ObjectSerializer
import com.appunite.websocket.rx.`object`.RxObjectWebSockets
import com.google.gson.Gson
import com.mekhedov.sasha.exness.common.GsonObjectSerializer
import com.mekhedov.sasha.exness.common.RxBus
import com.mekhedov.sasha.exness.mvp.MainModel
import com.mekhedov.sasha.exness.mvp.MainPresenter
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.Request
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

    /*@Provides
    @Singleton
    fun provideWebSocket() : RxObjectWebSockets  {
        var request : Request = Request.Builder()
                .get()
                .url("wss://quotes.exness.com:18400")
                .build()
        val rxWebSockets : RxWebSockets = RxWebSockets(OkHttpClient(), request)
        val serializer : ObjectSerializer = GsonObjectSerializer(Gson(), MainModel::class.java)
        val webSockets : RxObjectWebSockets = RxObjectWebSockets(rxWebSockets, serializer)
        return webSockets
    }*/

   /* @Provides
    @Singleton
    fun provideListService() : ListService  {
        val logging = HttpLoggingInterceptor()
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val httpClient = OkHttpClient.Builder()

        // add logging as last interceptor
        httpClient.addInterceptor(logging)

        val retrofit = Retrofit.Builder()
                .baseUrl(Data.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .client(httpClient.build())
                .build()
        return retrofit.create(ListService::class.java)
    }*/

}
