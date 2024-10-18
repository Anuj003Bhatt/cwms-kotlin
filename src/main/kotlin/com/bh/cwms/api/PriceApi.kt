package com.bh.cwms.api

import retrofit2.http.GET
import retrofit2.http.Headers
import rx.Single

interface PriceApi {
    @GET("v1/bpi/currentprice.json")
    @Headers("Content-Type: application/json")
    fun getBitCoinPrice(): Single<String?>?
}