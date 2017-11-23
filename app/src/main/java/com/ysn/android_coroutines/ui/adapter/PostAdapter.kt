package com.ysn.android_coroutines.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ysn.android_coroutines.R
import com.ysn.android_coroutines.network.Post

/**
 * Created by yudisetiawan on 11/23/17.
 */
class PostAdapter(var posts: List<Post> = ArrayList<Post>(),
                  var listener: PostClickListener): RecyclerView.Adapter<PostViewHolder>() {

    fun setElements(elements: List<Post>) {
        posts = elements
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.post_item, parent, false)
        return PostViewHolder(
                view,
                view.findViewById(R.id.text_view_title_post_item),
                view.findViewById(R.id.text_view_body_post_item)
                )
    }

    override fun onBindViewHolder(holder: PostViewHolder?, position: Int) {
        val post = posts[position]
        holder?.run {
            title.text = post.title
            body.text = post.body
            view.setOnClickListener {
                listener.onPostClicked(post)
            }
        }
    }

    override fun getItemCount(): Int = posts.size

}

interface PostClickListener {

    fun onPostClicked(post: Post)

}

class PostViewHolder(val view: View, val title: TextView, val body: TextView): RecyclerView.ViewHolder(view)