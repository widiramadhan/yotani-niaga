package com.javaindoku.yotaniniaga.view.news.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.databinding.ItemLoadingMoreBinding
import com.javaindoku.yotaniniaga.databinding.ItemNewsBinding
import com.javaindoku.yotaniniaga.model.news.NewsData
import com.javaindoku.yotaniniaga.service.network.NetworkState
import com.javaindoku.yotaniniaga.service.network.Status
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import java.text.SimpleDateFormat

class NewsAdapter (private val retry: ((activity: Activity) -> Unit), private val onClickNews: OnClickNews) : PagedListAdapter<NewsData, RecyclerView.ViewHolder>(diffUtil) {
    private var networkState: NetworkState? = null
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<NewsData>() {
            override fun areItemsTheSame(
                oldItem: NewsData,
                newItem: NewsData
            ): Boolean {
                return false
            }

            override fun areContentsTheSame(
                oldItem: NewsData,
                newItem: NewsData
            ): Boolean {
                return false
            }

        }
    }

    class ItemViewHolder(private val binding: ItemNewsBinding, private val onClickNews: OnClickNews) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(
            item: NewsData
        ) {
            Glide.with(itemView).load(item.newsImage).placeholder(R.color.colorLoading).into(binding.imgPhotos)
            binding.lblTitleNews.text = item.newsTitle
            //            val dateString = "2020-08-21 15:48:33 +07:00"
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val dateFormated = simpleDateFormat.parse(item.newsDate)
            val simpleDateFormat2 = SimpleDateFormat("EEEE, dd MMMM yyyy | HH:mm Z")
            val dateShow = simpleDateFormat2.format(dateFormated).replace("+0700", "WIB").replace("+0800", "WITA").replace("+0900", "WIT")
            binding.lblDateNews.text = dateShow
            itemView.setOnSafeClickListener {
                onClickNews.toDetailNews(item)
            }
        }
    }

    class ItemLoadingMore(private val binding: ItemLoadingMoreBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(networkState: NetworkState?) {
            if(networkState!=null){
                if(networkState.msg!=null && networkState.msg.contains("failed to connect",true)){
                    binding.lblNetworkStatus.text = itemView.context.getText(R.string.noIntenetAccess)
                    binding.imageView11.visibility = View.GONE
                }else{
                    binding.lblNetworkStatus.text = if(networkState?.status == Status.RUNNING) itemView.resources.getString(
                        R.string.srLoading) else networkState?.status.toString()
                    val rotate = RotateAnimation(360f, 0f, Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f)

                    rotate.duration = 900
                    rotate.repeatCount = Animation.INFINITE
                    binding.imageView11.startAnimation(rotate)
                }
            }else{
                binding.lblNetworkStatus.text = if(networkState?.status == Status.RUNNING) itemView.resources.getString(
                    R.string.srLoading) else networkState?.status.toString()
                val rotate = RotateAnimation(360f, 0f, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f)

                rotate.duration = 900
                rotate.repeatCount = Animation.INFINITE
                binding.imageView11.startAnimation(rotate)
            }
        }
    }

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    fun setNetworkState(networkState: NetworkState?) {
        this.networkState = networkState
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_news -> {
                val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ItemViewHolder(binding, onClickNews)
            }
            R.layout.item_loading_more -> {
                val binding = ItemLoadingMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ItemLoadingMore(binding)
            }
            else -> throw IllegalAccessException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_news -> {
                getItem(position)?.let { priceTbs ->
                    (holder as ItemViewHolder).bindItem(
                        priceTbs
                    )
                }
            }
            R.layout.item_loading_more -> {
                (holder as ItemLoadingMore).bindItem(networkState)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.item_loading_more
        } else {
            R.layout.item_news
        }
    }

    interface OnClickNews {
        fun toDetailNews(newsData: NewsData)
    }
}