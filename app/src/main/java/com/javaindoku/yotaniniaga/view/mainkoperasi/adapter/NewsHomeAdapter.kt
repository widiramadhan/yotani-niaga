package com.javaindoku.yotaniniaga.view.mainkoperasi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.databinding.ItemNewsHomeBinding
import com.javaindoku.yotaniniaga.model.news.NewsData
import com.javaindoku.yotaniniaga.view.news.adapter.NewsAdapter
import java.text.SimpleDateFormat

class NewsHomeAdapter (private val photos: List<NewsData>, private val newsClick: NewsAdapter.OnClickNews) : RecyclerView.Adapter<NewsHomeAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemNewsHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding, newsClick)
    }

    class ItemViewHolder(private val binding: ItemNewsHomeBinding, private val newsClick: NewsAdapter.OnClickNews) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(
            item: NewsData
        ) {
            Glide.with(itemView).load(item.newsImage).placeholder(R.color.colorLoading).into(binding.imgPhotos)
            binding.lblTitleNews.text = item.newsTitle
//            val dateString = "2020-08-21 15:48:33 GMT+07:00"
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val dateFormated = simpleDateFormat.parse(item.newsDate)
            val simpleDateFormat2 = SimpleDateFormat("EEEE, dd MMMM yyyy | HH:mm Z")
            val dateShow = simpleDateFormat2.format(dateFormated).replace("+0700", "WIB").replace("+0800", "WITA").replace("+0900", "WIT")
            binding.lblDateNews.text = dateShow
            itemView.setOnClickListener {
                newsClick.toDetailNews(item)
            }
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item: NewsData = photos[position]
        holder.bindItem(item)
    }

    override fun getItemCount(): Int {
        return if(photos.size>5) 5 else photos.size
    }
}