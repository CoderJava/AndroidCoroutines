package com.ysn.android_coroutines.ui.activity

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ysn.android_coroutines.R
import com.ysn.android_coroutines.experimental.Android
import com.ysn.android_coroutines.network.Post
import com.ysn.android_coroutines.network.SampleClient
import com.ysn.android_coroutines.network.api.Api
import com.ysn.android_coroutines.ui.adapter.PostAdapter
import com.ysn.android_coroutines.ui.adapter.PostClickListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.gildor.coroutines.retrofit.awaitResponse
import java.io.IOException

class MainActivity : AppCompatActivity(), PostClickListener, View.OnClickListener {

    private lateinit var postAdapter: PostAdapter
    private lateinit var postLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        initListeners()
    }

    private fun initListeners() {
        button_okhttp_activity_main.setOnClickListener(this)
        button_retrofit_activity_main.setOnClickListener(this)
    }

    private fun initViews() {
        postAdapter = PostAdapter(listener = this)
        postLayoutManager = LinearLayoutManager(this)
        recycler_view_post_list_activity_main.let {
            it.setHasFixedSize(true)
            it.layoutManager = postLayoutManager
            it.adapter = postAdapter
            it.visibility = View.GONE
        }
        progress_bar_activity_main.visibility = View.GONE
    }

    override fun onClick(view: View?) {
        view!!.run {
            when (id) {
                R.id.button_okhttp_activity_main -> {
                    getDataWithOkHttp()
                }
                R.id.button_retrofit_activity_main -> {
                    getDataWithRetrofit()
                }
                else -> {
                    /** nothing to do in here */
                }
            }
        }
    }

    private fun getDataWithRetrofit(): Job {
        val api = Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api::class.java)
        return launch(Android) {
            try {
                showLoading()
                delay(5000)

                val dataApi = async(CommonPool) {
                    val request = api.getData()
                    val response = request.awaitResponse()
                    val postsType = object : TypeToken<List<Post>>() {}.type
                    Gson().fromJson<List<Post>>(response.body()!!.string(), postsType)
                }

                // Will suspend until the call is finished
                postAdapter.setElements(dataApi.await())
                postAdapter.notifyDataSetChanged()
                hideLoading()
            } catch (ioe: IOException) {
                toast(message = "Phone not connected or service down", duration = Toast.LENGTH_LONG)
            }
        }
    }

    private fun getDataWithOkHttp() {
        launch(Android) {
            try {
                showLoading()
                val result = SampleClient.fetchPostsWithCoroutines()

                // Will suspend until the call is finished
                postAdapter.setElements(result.await())
                postAdapter.notifyDataSetChanged()
                hideLoading()
            } catch (exception: IOException) {
                toast(message = "Phone not connected or service down", duration = Toast.LENGTH_LONG)
            }
        }
    }

    private fun hideLoading() {
        recycler_view_post_list_activity_main.visibility = View.VISIBLE
        progress_bar_activity_main.visibility = View.GONE
    }

    private fun showLoading() {
        recycler_view_post_list_activity_main.visibility = View.GONE
        progress_bar_activity_main.visibility = View.VISIBLE
    }

    override fun onPostClicked(post: Post) {
        toast(message = "Clicked ${post.id}", duration = Toast.LENGTH_LONG)
    }

    fun Activity.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, duration)
                .show()
    }

}
