package com.ysn.android_coroutines.network

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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

    private val TAG = javaClass.simpleName
    val client = OkHttpClient()

    fun fetchPostsWithCoroutines(): Deferred<List<Post>> {
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

    fun fetchPostsWithRx() {
        Observable
                .create<Boolean> { e: ObservableEmitter<Boolean> ->
                    val request = Request.Builder()
                            .url("https://jsonplaceholder.typicode.com/posts")
                            .build()
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        e.onNext(true)
                    } else {
                        e.onNext(false)
                    }
                    e.onComplete()
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { isSuccessful: Boolean ->
                            when (isSuccessful) {
                                true -> {
                                    Log.d(TAG, "Do something in here is isSuccessful true")
                                }
                                else -> {
                                    Log.d(TAG, "Do something in here is isSuccessful false")
                                }
                            }
                        },
                        { throwable: Throwable? ->
                            throwable!!.run {
                                printStackTrace()
                                Log.d(TAG, "Throwable message: $message")
                            }
                        },
                        {
                            Log.d(TAG, "onComplete")
                        }
                )
    }


}

data class Post(
        val id: Int,
        val userId: Int,
        val title: String,
        val body: String
)