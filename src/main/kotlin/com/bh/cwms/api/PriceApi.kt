package com.bh.cwms.api

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import rx.Single

interface PriceApi {
    @GET("/v1/tickers")
    @Headers("Content-Type: application/json")
    fun getQuotes(
        @Query("quotes") quote:String
    ): Single<String?>?
}