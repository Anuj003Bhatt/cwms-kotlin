package com.bh.cwms.config

import com.bh.cwms.api.PriceApi
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.time.Duration

@Configuration
class ApiConfig {
    private val callAdapterFactory = RxJavaCallAdapterFactory.create()
    private val converterFactory = ScalarsConverterFactory.create()

    private val httpClient = OkHttpClient()

    protected fun <T> createApi(baseUrl: String?, timeout: Long, clazz: Class<T>): T {
        val duration = Duration.ofMillis(timeout)
        val clientBuilder: OkHttpClient.Builder = httpClient.newBuilder()
            .callTimeout(duration)
            .connectTimeout(duration)
            .readTimeout(duration)
            .writeTimeout(duration)
        return Retrofit.Builder()
            .baseUrl(baseUrl!!)
            .addCallAdapterFactory(callAdapterFactory)
            .addConverterFactory(converterFactory)
            .client(clientBuilder.build())
            .build()
            .create(clazz)
    }

    @Bean
    fun priceApi(
        @Value("\${ticker.api}") baseUrl: String,
        @Value("\${ticker.api.timeout}") timeout: Long
    ) = createApi(baseUrl, timeout, PriceApi::class.java)
}