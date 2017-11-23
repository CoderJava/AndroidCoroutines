package com.ysn.android_coroutines.network

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Created by yudisetiawan on 11/23/17.
 */
object SampleClient {

    val client = OkHttpClient()

    fun fetchPosts(): Deferred<List<Post>> {
        return async(CommonPool) {
            delay(500)
            val request = Request.Builder()
                    .url("https://jsonplaceholder.typicode.com/posts")
                    .build()
            val response = client.newCall(request).execute()
            val postsType = object : TypeToken<List<Post>>() {}.type
            Gson().fromJson<List<Post>>(response.body()!!.string(), postsType)
        }
    }

}

data class Post(
        val id: Int,
        val userId: Int,
        val title: String,
        val body: String
)