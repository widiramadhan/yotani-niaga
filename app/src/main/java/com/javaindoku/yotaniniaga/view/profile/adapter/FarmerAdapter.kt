package com.javaindoku.yotaniniaga.view.profile.adapter

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
import com.javaindoku.yotaniniaga.databinding.ItemDeliveryKoperasiBinding
import com.javaindoku.yotaniniaga.databinding.ItemFarmerProfileBinding
import com.javaindoku.yotaniniaga.databinding.ItemLoadingMoreBinding
import com.javaindoku.yotaniniaga.model.profile.Farmer
import com.javaindoku.yotaniniaga.model.profilePetaniDetail.Data
import com.javaindoku.yotaniniaga.service.network.NetworkState
import com.javaindoku.yotaniniaga.service.network.Status
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener

class FarmerAdapter(
    private val retry: ((activity: Activity) -> Unit),
    private val activity: Activity,
    private val profileFarmerClick: ProfileFarmerClick
) : PagedListAdapter<Data, RecyclerView.ViewHolder>(diffUtil) {
    private var networkState: NetworkState? = null

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Data>() {
            override fun areItemsTheSame(
                oldItem: Data,
                newItem: Data
            ): Boolean {
                return false
            }

            override fun areContentsTheSame(
                oldItem: Data,
                newItem: Data
            ): Boolean {
                return false
            }

        }
    }

    class ItemViewHolder(
        private val binding: ItemFarmerProfileBinding,
        private val profileFarmerClick: ProfileFarmerClick
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(
            item: Data
        ) {
            binding.lblName.text = item.namaPetani
            binding.lblPhoneNo.text = item.mobile
            binding.farmerLayout.setOnClickListener { profileFarmerClick.onClick(item) }
            binding.editButton.setOnClickListener { profileFarmerClick.edit(item) }
            binding.deleteButton.setOnClickListener { profileFarmerClick.delete(item) }
        }
    }

    class ItemLoadingMore(
        private val binding: ItemLoadingMoreBinding,
        private val retry: ((activity: Activity) -> Unit),
        private val activity: Activity
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(networkState: NetworkState?) {
            if (networkState != null) {
                if (networkState.msg != null && networkState.msg.contains(
                        "failed to connect",
                        true
                    )
                ) {
                    binding.lblNetworkStatus.text =
                        itemView.context.getText(R.string.noIntenetAccess)
                    binding.imageView11.visibility = View.GONE
                } else {
                    binding.lblNetworkStatus.text =
                        if (networkState?.status == Status.RUNNING) itemView.resources.getString(
                            R.string.srLoading
                        ) else networkState?.status.toString()
                    val rotate = RotateAnimation(
                        360f, 0f, Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f
                    )

                    rotate.duration = 900
                    rotate.repeatCount = Animation.INFINITE
                    binding.imageView11.startAnimation(rotate)
                }
            } else {
                binding.lblNetworkStatus.text =
                    if (networkState?.status == Status.RUNNING) itemView.resources.getString(
                        R.string.srLoading
                    ) else networkState?.status.toString()
                val rotate = RotateAnimation(
                    360f, 0f, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
                )

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
            R.layout.item_farmer_profile -> {
                val binding = ItemFarmerProfileBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ItemViewHolder(binding, profileFarmerClick)
            }
            R.layout.item_loading_more -> {
                val binding = ItemLoadingMoreBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ItemLoadingMore(binding, retry, activity)
            }
            else -> throw IllegalAccessException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_farmer_profile -> {
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
            R.layout.item_farmer_profile
        }
    }

    interface ProfileFarmerClick {
        fun edit(farmer: Data)
        fun delete(farmer: Data)
        fun onClick(farmer: Data)
    }

}