package com.javaindoku.yotaniniaga.view.delivery.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.databinding.ItemLoadingMoreBinding
import com.javaindoku.yotaniniaga.databinding.ItemDeliveryKoperasiBinding
import com.javaindoku.yotaniniaga.model.deliveryreservation.DeliveryData
import com.javaindoku.yotaniniaga.service.network.NetworkState
import com.javaindoku.yotaniniaga.service.network.Status
import com.javaindoku.yotaniniaga.utilities.CustomProfile
import com.javaindoku.yotaniniaga.utilities.StringFormat
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import com.vansuita.pickimage.util.Util.gone

class DeliveryKoperasiAdapter (private val retry: ((activity: Activity) -> Unit), private val activity: Activity, private val deliveryKoperasiClick: DeliveryKoperasiClick) : PagedListAdapter<DeliveryData, RecyclerView.ViewHolder>(diffUtil) {
    private var networkState: NetworkState? = null
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<DeliveryData>() {
            override fun areItemsTheSame(
                oldItem: DeliveryData,
                newItem: DeliveryData
            ): Boolean {
                return false
            }

            override fun areContentsTheSame(
                oldItem: DeliveryData,
                newItem: DeliveryData
            ): Boolean {
                return false
            }

        }
    }

    class ItemViewHolder(private val binding: ItemDeliveryKoperasiBinding, private val deliveryKoperasiClick: DeliveryKoperasiClick) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(
            item: DeliveryData
        ) {
            binding.lblDate.text = StringFormat.getLocalDateFormatFromLaravel(item.tanggalPengiriman, "in")
            binding.lblStatus.text = item.statusName
            binding.lblInvoiceNo.text = item.noReservasi
            binding.lblName.text = item.namaPabrik
            Glide.with(itemView.context).load(item.foto).into(binding.imgSupplyDemands)
            CustomProfile.showRemoteImageUsingGlide(
                itemView.context,
                binding.imgSupplyDemands,
                item.foto,
                R.drawable.ic_building
            )
            binding.lblSupply.text = "${item.tonasi} kg"
            binding.btnReschedule.setOnSafeClickListener { deliveryKoperasiClick.reschedule(item) }
            binding.imgShowQrCode.setOnSafeClickListener { deliveryKoperasiClick.showBarcode(item) }
            binding.btnViewInvoice.setOnSafeClickListener { deliveryKoperasiClick.viewInvoice(item) }

            if (item.status == "1"){
                binding.btnReschedule.setVisibility(View.VISIBLE)
                binding.imgShowQrCode.setVisibility(View.VISIBLE)
                binding.btnViewInvoice.setVisibility(View.GONE)
            }else{
                binding.btnReschedule.setVisibility(View.GONE)
                binding.imgShowQrCode.setVisibility(View.GONE)
                binding.btnViewInvoice.setVisibility(View.VISIBLE)
            }

        }
    }

    class ItemLoadingMore(private val binding: ItemLoadingMoreBinding, private val retry: ((activity: Activity) -> Unit), private val activity: Activity) : RecyclerView.ViewHolder(binding.root) {
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
            R.layout.item_delivery_koperasi -> {
                val binding = ItemDeliveryKoperasiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ItemViewHolder(binding, deliveryKoperasiClick)
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
            R.layout.item_delivery_koperasi -> {
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
            R.layout.item_delivery_koperasi
        }
    }

    interface DeliveryKoperasiClick {
        fun reschedule(deliveryKoperasi: DeliveryData)
        fun showBarcode(deliveryKoperasi: DeliveryData)
        fun viewInvoice(deliveryKoperasi: DeliveryData)
    }

}