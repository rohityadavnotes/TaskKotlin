package com.task.data.remote

import android.util.Log
import com.google.gson.GsonBuilder
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import com.task.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private var retrofit: Retrofit? = null

    fun getClient(baseUrl: String): Retrofit? {
        if (retrofit == null) {
            retrofit = getRetrofitInstance(baseUrl)
        }
        return retrofit
    }

    private fun getRetrofitInstance(baseUrl: String): Retrofit {
        val okHttpClientBuilder = getOkHttpClientBuilderInstance();
        val okHttpClient = okHttpClientBuilder.build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        val builder = Retrofit.Builder()
        builder.baseUrl(baseUrl)
        builder.client(okHttpClient)
        builder.addConverterFactory(GsonConverterFactory.create(gson))
        builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create())

        return builder.build()
    }

    private fun getOkHttpClientBuilderInstance(): OkHttpClient.Builder {

        val okHttpClientBuilder = OkHttpClient.Builder()

        okHttpClientBuilder.connectTimeout(RemoteConfiguration.HTTP_CONNECT_TIMEOUT.toLong(), TimeUnit.MINUTES)
        okHttpClientBuilder.writeTimeout(RemoteConfiguration.HTTP_WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
        okHttpClientBuilder.readTimeout(RemoteConfiguration.HTTP_READ_TIMEOUT.toLong(), TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            val interceptor: Interceptor = LoggingInterceptor.Builder().setLevel(Level.BASIC).log(Log.VERBOSE).build()
            okHttpClientBuilder.addInterceptor(interceptor)
        }

        return okHttpClientBuilder
    }
}