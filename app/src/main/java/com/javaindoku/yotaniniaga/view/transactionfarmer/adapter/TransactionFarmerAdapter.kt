package com.javaindoku.yotaniniaga.view.transactionfarmer.adapter

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
import com.javaindoku.yotaniniaga.databinding.ItemTransactionFarmerBinding
import com.javaindoku.yotaniniaga.model.transactionfarmer.TransactionFarmer
import com.javaindoku.yotaniniaga.service.network.NetworkState
import com.javaindoku.yotaniniaga.service.network.Status
import com.javaindoku.yotaniniaga.utilities.CustomProfile
import com.javaindoku.yotaniniaga.utilities.StringFormat
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener

class TransactionFarmerAdapter(
    private val retry: ((activity: Activity) -> Unit),
    private val activity: Activity,
    private val transactionKoperasiInterface: TransactionFarmerInterface
) : PagedListAdapter<TransactionFarmer, RecyclerView.ViewHolder>(diffUtil) {
    private var networkState: NetworkState? = null

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<TransactionFarmer>() {
            override fun areItemsTheSame(
                oldItem: TransactionFarmer,
                newItem: TransactionFarmer
            ): Boolean {
                return false
            }

            override fun areContentsTheSame(
                oldItem: TransactionFarmer,
                newItem: TransactionFarmer
            ): Boolean {
                return false
            }

        }
    }

    class ItemViewHolder(
        private val binding: ItemTransactionFarmerBinding,
        private val transactionFarmerInterface: TransactionFarmerInterface
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(
            item: TransactionFarmer
        ) {
            binding.lblCompanyName.text = item.companyName
            CustomProfile.showRemoteImageUsingGlide(
                itemView.context,
                binding.imgTransactionFarmer,
                item.image,
                R.drawable.ic_building
            )
            binding.lblQuota.text = "${item.quota} kg"
            binding.lblPrice.text = "Rp ${StringFormat.formatCurrencyNumber2Point(item.price)}"
            binding.lblQuotaFill.text = "${item.quotaFill}/${item.quota} kg"
            binding.btnSendTbs.setOnSafeClickListener {
                transactionFarmerInterface.toSendStockSupply(item)
            }
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
            R.layout.item_transaction_farmer -> {
                val binding = ItemTransactionFarmerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ItemViewHolder(binding, transactionKoperasiInterface)
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
            R.layout.item_transaction_farmer -> {
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
            R.layout.item_transaction_farmer
        }
    }

    interface TransactionFarmerInterface {
        fun toSendStockSupply(transactionFarmer: TransactionFarmer)
    }
}