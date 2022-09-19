package com.javaindoku.yotaniniaga.view.sendstocksupplyfarmer.dialog

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.resource.bitmap.BitmapEncoder
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.databinding.DialogBarcodeBinding
import com.javaindoku.yotaniniaga.databinding.DialogConfirmAddReservasiBinding
import com.javaindoku.yotaniniaga.databinding.DialogInvoiceDetailBinding
import com.javaindoku.yotaniniaga.databinding.DialogRescheduleBinding
import com.javaindoku.yotaniniaga.model.deliveryreservation.DeliveryData
import com.javaindoku.yotaniniaga.model.deliveryreservation.DeliveryReservationEditBody
import com.javaindoku.yotaniniaga.model.invoice.Invoice
import com.javaindoku.yotaniniaga.model.sendstocksupplyfarmer.DataConfirmReservasi
import com.javaindoku.yotaniniaga.utilities.StringFormat
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import com.javaindoku.yotaniniaga.view.addGarden.adapter.DocumentListAdapter
import com.javaindoku.yotaniniaga.view.sendstocksupplyfarmer.adapter.ConfirmReservasiListAdapter
import com.javaindoku.yotaniniaga.viewmodel.DeliveryKoperasiViewModel
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.text.SimpleDateFormat
import java.util.*


object ConfirmReservasiDialog {
    private lateinit var documentListAdapter: ConfirmReservasiListAdapter

    fun listTransaksi(
        activity: Activity,
        cancelable: Boolean,
        labelPositiveButton: String? = null,
        labelNegativeButton: String? = null,
        positiveAction: ((activity: Activity) -> Unit)? = null,
        negativeAction: ((activity: Activity) -> Unit)? = null,
        data: List<DataConfirmReservasi>,
    ) : Dialog {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogConfirmAddReservasiBinding.inflate(LayoutInflater.from(activity))
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(cancelable)
        binding.imgClose.setOnSafeClickListener {
            dialog.dismiss()
        }

        if (labelPositiveButton != null) {
            binding.btnConfirmationPositive.text = labelPositiveButton
            binding.btnConfirmationPositive.setOnClickListener {
                positiveAction?.let { it1 -> it1(activity) }
                dialog.dismiss()
            }
        } else {
            binding.btnConfirmationPositive.visibility = View.GONE
        }
        if (labelNegativeButton != null) {
            binding.btnConfirmationNegative.text = labelNegativeButton
            binding.btnConfirmationNegative.setOnClickListener {
                negativeAction?.let { it1 -> it1(activity) }
                dialog.dismiss()
            }
        } else {
            binding.btnConfirmationNegative.visibility = View.GONE
        }
        
        documentListAdapter = ConfirmReservasiListAdapter(activity, data)
        binding.rvConfirmReservasi.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvConfirmReservasi.adapter = documentListAdapter
        documentListAdapter.refreshList(data)

        return dialog
    }
}