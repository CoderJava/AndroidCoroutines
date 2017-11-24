package com.ysn.android_coroutines.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

/**
 * Created by yudisetiawan on 11/24/17.
 */
interface Api {

    @GET("posts")
    fun getData(): Call<ResponseBody>

}