package com.example.storyapp.ui.liststory

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.databinding.ItemListStoryBinding
import com.example.storyapp.nonui.model.ListModel
import com.example.storyapp.nonui.model.UserModel
import com.example.storyapp.ui.listdetail.DetailActivity

class ListAdapter(private val listUser: ArrayList<ListModel>): RecyclerView.Adapter<ListAdapter.ListViewHolder>(){

    class ListViewHolder(var binding: ItemListStoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemListStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (name, description, photoUrl) = listUser[position]

        Glide.with(holder.itemView.context).load(photoUrl).into(holder.binding.imageList)
        holder.binding.tvName.text = name

        val story = ListModel(name, description, photoUrl)
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailActivity::class.java)
            intent.putExtra(DetailActivity.STORY_DETAIL_EXTRA, story)
            holder.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount() = listUser.size
}