package com.javaindoku.yotaniniaga.view.listpricetbs.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.databinding.ItemLoadingMoreBinding
import com.javaindoku.yotaniniaga.databinding.ItemPriceTbsBinding
import com.javaindoku.yotaniniaga.service.network.NetworkState
import com.javaindoku.yotaniniaga.service.network.Status
import com.javaindoku.yotaniniaga.view.pricetbs.PriceTbs

class ListPriceAdapter (private val retry: ((activity: Activity) -> Unit), private val activity: Activity) : PagedListAdapter<PriceTbs, RecyclerView.ViewHolder>(diffUtil) {
    private var networkState: NetworkState? = null
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<PriceTbs>() {
            override fun areItemsTheSame(
                oldItem: PriceTbs,
                newItem: PriceTbs
            ): Boolean {
                return false
            }

            override fun areContentsTheSame(
                oldItem: PriceTbs,
                newItem: PriceTbs
            ): Boolean {
                return false
            }

        }
    }

    class ItemViewHolder(private val binding: ItemPriceTbsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(
            item: PriceTbs
        ) {
            binding.lblYears.text = item.years
            binding.lblPrice.text = item.price
        }
    }

    class ItemLoadingMore(private val binding: ItemLoadingMoreBinding, private val retry: ((activity: Activity) -> Unit), private val activity: Activity) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(networkState: NetworkState?) {
            if(networkState!=null){
                if(networkState.msg!=null && networkState.msg.contains("failed to connect",true)){
                    binding.lblNetworkStatus.text = itemView.context.getText(R.string.noIntenetAccess)
                    binding.imageView11.visibility = View.GONE
                }else{
                    binding.lblNetworkStatus.text = if(networkState?.status == Status.RUNNING) itemView.resources.getString(R.string.srLoading) else networkState?.status.toString()
                    val rotate = RotateAnimation(360f, 0f, Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f)

                    rotate.duration = 900
                    rotate.repeatCount = Animation.INFINITE
                    binding.imageView11.startAnimation(rotate)
                }
            }else{
                binding.lblNetworkStatus.text = if(networkState?.status == Status.RUNNING) itemView.resources.getString(R.string.srLoading) else networkState?.status.toString()
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
            R.layout.item_price_tbs -> {
                val binding = ItemPriceTbsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ItemViewHolder(binding)
            }
            R.layout.item_loading_more -> {
                val binding = ItemLoadingMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ItemLoadingMore(binding, retry, activity)
            }
            else -> throw IllegalAccessException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_price_tbs -> {
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
            R.layout.item_price_tbs
        }
    }

}