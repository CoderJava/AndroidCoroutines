package com.ysn.android_coroutines.ui.activity

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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

class MainActivity : AppCompatActivity(), PostClickListener {

    private lateinit var postAdapter: PostAdapter
    private lateinit var postLayoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        postAdapter = PostAdapter(listener = this)
        postLayoutManager = LinearLayoutManager(this)

        recycler_view_post_list_activity_main.let {
            it.setHasFixedSize(true)
            it.layoutManager = postLayoutManager
            it.adapter = postAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        launch (Android) {
            try {
                val result = SampleClient.fetchPostsWithCoroutines()

                // Will suspend until the call is finished
                postAdapter.setElements(result.await())
                postAdapter.notifyDataSetChanged()
            } catch (exception: IOException) {
                toast(message = "Phone not connected or service down", duration = Toast.LENGTH_LONG)
            }
        }
    }

    override fun onPostClicked(post: Post) {
        toast(message = "Clicked ${post.id}", duration = Toast.LENGTH_LONG)
    }

    fun Activity.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, duration)
                .show()
    }

}
