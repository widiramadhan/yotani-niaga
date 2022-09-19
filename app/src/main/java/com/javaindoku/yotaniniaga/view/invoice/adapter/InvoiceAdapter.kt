package com.javaindoku.yotaniniaga.view.invoice.adapter

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
import com.javaindoku.yotaniniaga.databinding.ItemInvoiceBinding
import com.javaindoku.yotaniniaga.databinding.ItemLoadingMoreBinding
import com.javaindoku.yotaniniaga.model.deliveryreservation.DeliveryData
import com.javaindoku.yotaniniaga.model.invoice.Invoice
import com.javaindoku.yotaniniaga.service.network.NetworkState
import com.javaindoku.yotaniniaga.service.network.Status
import com.javaindoku.yotaniniaga.utilities.ConstValue
import com.javaindoku.yotaniniaga.utilities.StringFormat
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import com.javaindoku.yotaniniaga.view.news.adapter.NewsAdapter

class InvoiceAdapter (private val retry: ((activity: Activity) -> Unit), private val invoiceClick: InvoiceClick, private val language: String) : PagedListAdapter<Invoice, RecyclerView.ViewHolder>(diffUtil) {
    private var networkState: NetworkState? = null
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Invoice>() {
            override fun areItemsTheSame(
                oldItem: Invoice,
                newItem: Invoice
            ): Boolean {
                return false
            }

            override fun areContentsTheSame(
                oldItem: Invoice,
                newItem: Invoice
            ): Boolean {
                return false
            }

        }
    }

    class ItemViewHolder(private val binding: ItemInvoiceBinding,
                         private val invoiceClick: InvoiceClick,
                         private val language: String) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(
            item: Invoice
        ) {
            binding.lblDate.text = StringFormat.getLocalDateFormatFromLaravel(item.dueDate.toString(), language)
            binding.lblInvoiceNo.text = item.invoiceNo
            Glide.with(itemView.context)
                .load(R.drawable.ic_invoice_item)
                .into(binding.imgInvoice)
            binding.lblBuyPrice.text = "${itemView.context.getString(R.string.total_buy)} ${ConstValue.CURRENCY} ${StringFormat.formatCurrencyNumber(item.totalPrice)}"
            binding.lblPayPrice.text = "${itemView.context.getString(R.string.total_pay)} ${ConstValue.CURRENCY} ${StringFormat.formatCurrencyNumber(item.totalPaid)}"
            binding.lblCompanyName.text = item.factoryName
            binding.lblStatus.text = item.statusName
            itemView.setOnSafeClickListener {
                invoiceClick.viewDetail(item)
            }
        }
    }

    class ItemLoadingMore(private val binding: ItemLoadingMoreBinding, private val retry: ((activity: Activity) -> Unit)) : RecyclerView.ViewHolder(binding.root) {
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
            R.layout.item_invoice -> {
                val binding = ItemInvoiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ItemViewHolder(binding, invoiceClick, language)
            }
            R.layout.item_loading_more -> {
                val binding = ItemLoadingMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ItemLoadingMore(binding, retry)
            }
            else -> throw IllegalAccessException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_invoice -> {
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
            R.layout.item_invoice
        }
    }

    interface InvoiceClick {
        fun viewDetail(invoice: Invoice)
    }

}