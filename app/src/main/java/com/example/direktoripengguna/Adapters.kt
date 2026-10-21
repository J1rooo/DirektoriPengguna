package com.example.direktoripengguna

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.direktoripengguna.data.Post
import com.example.direktoripengguna.data.User

// Adapter kartu pengguna (item_user.xml)
class UserAdapter(context: Context, private val data: List<User>) :
    ArrayAdapter<User>(context, 0, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.item_user, parent, false)
        val u = data[position]
        v.findViewById<TextView>(R.id.tvNama).text = u.name
        v.findViewById<TextView>(R.id.tvDetail).text = "@${u.username} · ${u.email}"
        return v
    }
}

// Adapter kartu postingan (item_post.xml)
class PostAdapter(context: Context, private val data: List<Post>) :
    ArrayAdapter<Post>(context, 0, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.item_post, parent, false)
        val p = data[position]
        v.findViewById<TextView>(R.id.tvJudulPost).text = p.title
        v.findViewById<TextView>(R.id.tvIdPost).text = "post id ${p.id}"
        return v
    }
}
