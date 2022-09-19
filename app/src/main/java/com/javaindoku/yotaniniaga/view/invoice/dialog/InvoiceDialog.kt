package com.javaindoku.yotaniniaga.view.invoice.dialog

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
import android.text.Html
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.load.resource.bitmap.BitmapEncoder
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.databinding.DialogBarcodeBinding
import com.javaindoku.yotaniniaga.databinding.DialogInvoiceDetailBinding
import com.javaindoku.yotaniniaga.databinding.DialogRescheduleBinding
import com.javaindoku.yotaniniaga.model.deliveryreservation.DeliveryData
import com.javaindoku.yotaniniaga.model.deliveryreservation.DeliveryReservationEditBody
import com.javaindoku.yotaniniaga.model.invoice.Invoice
import com.javaindoku.yotaniniaga.model.invoiceDetail.InvoiceDetail
import com.javaindoku.yotaniniaga.model.invoiceDetail.InvoiceDetailData
import com.javaindoku.yotaniniaga.utilities.ConstValue
import com.javaindoku.yotaniniaga.utilities.StringFormat
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import com.javaindoku.yotaniniaga.viewmodel.DeliveryKoperasiViewModel
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.text.SimpleDateFormat
import java.util.*


object InvoiceDialog {
    fun viewDetail(
        activity: Activity,
        cancelable: Boolean,
        labelPositiveButton: String? = null,
        labelNegativeButton: String? = null,
        positiveAction: ((activity: Activity) -> Unit)? = null,
        negativeAction: ((activity: Activity) -> Unit)? = null,
        invoice: Invoice,
        language: String
    ) : Dialog {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogInvoiceDetailBinding.inflate(LayoutInflater.from(activity))
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

        binding.lblNamePKS.text = ": ${invoice.factoryName}"
        binding.lblNoFaktur.text = ": ${invoice.invoiceNo}"
        binding.lblNoSPB.text = ": ${invoice.grnNumber}"
        binding.lblTglTerimaTBS.text = StringFormat.getLocalDateFormatFromLaravel(invoice.tanggalTerima.toString(), language) // tanggal terima tbs
        binding.lblKuantitasTBS.text = "${invoice.totalTonasi} kg"
        binding.lblTotalTransaksi.text = "${ConstValue.CURRENCY} ${StringFormat.formatCurrencyNumber(invoice.totalPrice)}"
        binding.lblJumlahtransfer.text = "${ConstValue.CURRENCY} ${StringFormat.formatCurrencyNumber(invoice.totalPaid)}"
        binding.lblNoPolisi.text = invoice.noTruk
        binding.lblHargaSatuan.text = "${ConstValue.CURRENCY} ${StringFormat.formatCurrencyNumber(invoice.hargaTbs)} /kg"
        binding.lblBiayaPlatform.text = "${ConstValue.CURRENCY} ${StringFormat.formatCurrencyNumber(invoice.biayaPlatform)} /kg"
        binding.lblStatus.text = invoice.statusName

        /*binding.lblName.text = invoice.supplierName
        binding.lblJumlahtransferDetail.text = "${ConstValue.CURRENCY} ${StringFormat.formatCurrencyNumber(invoice.totalPaid)}"
        binding.lblBankDetail.text = "${invoice.bankName}# ${invoice.noRekening} an ${invoice.bankAtasNama}"
        binding.lblTglPembayaran.text = StringFormat.getLocalDateFormatFromLaravel(invoice.dueDate.toString(), language)*/

        val notes = "<font color=#4285F4>Anda, <b>${invoice.supplierName}</b> akan menerima </font>" +
                            "<font color=#4D4D4D><b>${ConstValue.CURRENCY} ${StringFormat.formatCurrencyNumber(invoice.totalPaid)}</b></font>" +
                            "<font color=#4285F4> direkening </font>" +
                            "<font color=#4D4D4D><b>${invoice.bankName}# ${invoice.noRekening} an ${invoice.bankAtasNama}</b></font>" +
                            "<font color=#4285F4> pada tanggal </font>" +
                            "<font color=#4D4D4D><b>${StringFormat.getLocalDateFormatFromLaravel(invoice.dueDate.toString(), language)}</b></font>" +
                            "<font color=#4285F4> untuk transaksi ini.</font>"
        binding.notesInvoice.text = Html.fromHtml(notes)

        return dialog
    }

    // invoice detail from api
    fun viewDetailApi(
        activity: Activity,
        cancelable: Boolean,
        labelPositiveButton: String? = null,
        labelNegativeButton: String? = null,
        positiveAction: ((activity: Activity) -> Unit)? = null,
        negativeAction: ((activity: Activity) -> Unit)? = null,
        invoiceDetailData: InvoiceDetailData,
        language: String
    ) : Dialog {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogInvoiceDetailBinding.inflate(LayoutInflater.from(activity))
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

        binding.lblNamePKS.text = ": ${invoiceDetailData.namaPabrik}"
        binding.lblNoFaktur.text = ": ${invoiceDetailData.noInvoice}"
        binding.lblNoSPB.text = ": ${invoiceDetailData.noGrn}"
        binding.lblTglTerimaTBS.text = StringFormat.getLocalDateFormatFromLaravel(invoiceDetailData.tanggalTerima.toString(), language) // tanggal terima tbs
        binding.lblKuantitasTBS.text = "${invoiceDetailData.totalTonasi} kg"
        binding.lblTotalTransaksi.text = "${ConstValue.CURRENCY} ${invoiceDetailData.hargaTotal?.let {StringFormat.formatCurrencyNumber(it)}}"
        binding.lblJumlahtransfer.text = "${ConstValue.CURRENCY} ${invoiceDetailData.jumlahPembayaran?.let {StringFormat.formatCurrencyNumber(it)}}"
        binding.lblNoPolisi.text = invoiceDetailData.noTruk
        binding.lblHargaSatuan.text = "${ConstValue.CURRENCY} ${invoiceDetailData.hargaTbs?.let {StringFormat.formatCurrencyNumber(it)}} /kg"
        binding.lblBiayaPlatform.text = "${ConstValue.CURRENCY} ${invoiceDetailData.biayaPlatform?.let {StringFormat.formatCurrencyNumber(it)}} /kg"
        binding.lblStatus.text = invoiceDetailData.statusName

        /*binding.lblName.text = invoiceDetailData.supplierName
        binding.lblJumlahtransferDetail.text = "${ConstValue.CURRENCY} ${invoiceDetailData.jumlahPembayaran?.let{StringFormat.formatCurrencyNumber(it)}}"
        binding.lblBankDetail.text = "${invoiceDetailData.bankName}# ${invoiceDetailData.noRekening} an ${invoiceDetailData.bankAtasNama}"
        binding.lblTglPembayaran.text = StringFormat.getLocalDateFormatFromLaravel(invoiceDetailData.dueDate.toString(), language)*/

        val notes = "<font color=#4285F4>Anda, <b>${invoiceDetailData.supplierName}</b> akan menerima </font>" +
                "<font color=#4D4D4D><b>${ConstValue.CURRENCY} ${invoiceDetailData.jumlahPembayaran?.let{StringFormat.formatCurrencyNumber(it)}}</b></font>" +
                "<font color=#4285F4> direkening </font>" +
                "<font color=#4D4D4D><b>${invoiceDetailData.bankName}# ${invoiceDetailData.noRekening} an ${invoiceDetailData.bankAtasNama}</b></font>" +
                "<font color=#4285F4> pada tanggal </font>" +
                "<font color=#4D4D4D><b>${StringFormat.getLocalDateFormatFromLaravel(invoiceDetailData.dueDate.toString(), language)}</b></font>" +
                "<font color=#4285F4> untuk transaksi ini.</font>"
        binding.notesInvoice.text = Html.fromHtml(notes)

        return dialog
    }
}