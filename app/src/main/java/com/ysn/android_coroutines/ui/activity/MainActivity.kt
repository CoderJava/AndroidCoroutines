package com.ysn.android_coroutines.ui.activity

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.ysn.android_coroutines.R
import com.ysn.android_coroutines.experimental.Android
import com.ysn.android_coroutines.network.Post
import com.ysn.android_coroutines.network.SampleClient
import com.ysn.android_coroutines.ui.adapter.PostAdapter
import com.ysn.android_coroutines.ui.adapter.PostClickListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.launch
import java.io.IOException

class MainActivity : AppCompatActivity(), PostClickListener, View.OnClickListener {

    private lateinit var postAdapter: PostAdapter
    private lateinit var postLayoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        postAdapter = PostAdapter(listener = this)
        postLayoutManager = LinearLayoutManager(this)

        initViews()
        initListeners()
    }

    private fun initListeners() {
        button_okhttp_activity_main.setOnClickListener(this)
        button_retrofit_activity_main.setOnClickListener(this)
    }

    private fun initViews() {
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
                    TODO(reason = "do something in here")
                }
                else -> {
                    /** nothing to do in here */
                }
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
