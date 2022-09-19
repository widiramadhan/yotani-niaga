package com.javaindoku.yotaniniaga.view.delivery.dialog

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
import com.bumptech.glide.load.resource.bitmap.BitmapEncoder
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.databinding.DialogBarcodeBinding
import com.javaindoku.yotaniniaga.databinding.DialogRescheduleBinding
import com.javaindoku.yotaniniaga.model.deliveryreservation.DeliveryData
import com.javaindoku.yotaniniaga.model.deliveryreservation.DeliveryReservationEditBody
import com.javaindoku.yotaniniaga.utilities.StringFormat
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import com.javaindoku.yotaniniaga.viewmodel.DeliveryKoperasiViewModel
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.text.SimpleDateFormat
import java.util.*


object DeliveryDialog {
    fun rescheduleDialog(
        activity: Activity,
        cancelable: Boolean,
        labelPositiveButton: String? = null,
        labelNegativeButton: String? = null,
        positiveAction: ((activity: Activity) -> Unit)? = null,
        negativeAction: ((activity: Activity) -> Unit)? = null,
        deliveryData: DeliveryData,
        viewModel: DeliveryKoperasiViewModel,
        petaniId: String,
        userId: String
    ) : Dialog {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogRescheduleBinding.inflate(LayoutInflater.from(activity))
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

        binding.lblDate.text = StringFormat.dateFormatDdMmYyyy(deliveryData.tanggalPengiriman)
        binding.lblTime.text = StringFormat.dateFormatHhMm(deliveryData.tanggalPengiriman)

        if (labelPositiveButton != null) {
            binding.btnConfirmationPositive.text = labelPositiveButton
            binding.btnConfirmationPositive.setOnClickListener {
                positiveAction?.let { it1 -> it1(activity) }
                if(binding.lblDate.text.toString().isNullOrBlank()) {
                    Toast.makeText(activity, activity.getString(R.string.please_fill_date), Toast.LENGTH_SHORT).show()
                }
                else if (binding.lblTime.text.toString().isNullOrBlank())
                {
                    Toast.makeText(activity, activity.getString(R.string.please_fill_time), Toast.LENGTH_SHORT).show()
                }
                else
                {
                    viewModel.deliveryReservationEditBody.postValue(DeliveryReservationEditBody(
                        noReservasi = deliveryData.noReservasi,
                        petaniId = petaniId,
                        status = "1",
                        tanggalPengiriman = StringFormat.dateFormatLaravel("${binding.lblDate.text.toString()} ${binding.lblTime.text.toString()}"),
                        typeUser = "PTN",
                        userId = userId
                    ))
                }
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

        binding.edtDate.setOnClickListener {
            showDatePickerDialogSend(binding.lblDate, activity)
        }

        if(!binding.lblDate.text.toString().isNullOrBlank()) {
            binding.imgDate.setOnClickListener {
                binding.imgDate.setImageResource(R.drawable.ic_close_black_24dp)
                binding.lblDate.setText("")
            }
        }

        binding.lblDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s!!.isNotEmpty())
                {
                    binding.imgDate.setImageResource(R.drawable.ic_close_black_24dp)
                    binding.imgDate.setOnClickListener {
                        binding.lblDate.setText("")
                    }
                }
                else
                {
                    binding.imgDate.setImageResource(R.drawable.kalender)
                    binding.imgDate.setOnClickListener {
                        showDatePickerDialogSend(binding.lblDate, activity)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        binding.edtTime.setOnSafeClickListener {
            showTimePickerDialogSend(binding.lblTime, activity)
        }

        if(!binding.lblTime.text.toString().isNullOrBlank())
        {
            binding.imgTime.setOnClickListener {
                binding.imgTime.setImageResource(R.drawable.ic_close_black_24dp)
                binding.lblTime.setText("")
            }
        }

        binding.lblTime.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s!!.isNotEmpty())
                {
                    binding.imgTime.setImageResource(R.drawable.ic_close_black_24dp)
                    binding.imgTime.setOnClickListener {
                        binding.lblTime.setText("")
                    }
                }
                else
                {
                    binding.imgTime.setImageResource(R.drawable.ic_arrow_down)
                    binding.imgTime.setOnClickListener {
                        showTimePickerDialogSend(binding.lblTime, activity)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        return dialog
    }

    private fun showDatePickerDialogSend(textView: TextView, activity: Activity) {
        val c: Calendar = Calendar.getInstance()
        val mYear: Int = c.get(Calendar.YEAR)
        val mMonth: Int = c.get(Calendar.MONTH)
        val mDay: Int = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = activity.let {
            DatePickerDialog(
                it,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    c.set(Calendar.YEAR, year)
                    c.set(Calendar.MONTH, monthOfYear)
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val myFormat = "dd/MM/yyyy" //In which you need put here
                    val sdf = SimpleDateFormat(myFormat)
                    textView.text = sdf.format(c.time)
                }, mYear, mMonth, mDay
            )
        }
        datePickerDialog.show()
    }

    private fun showTimePickerDialogSend(textView: TextView, context: Context) {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)

            textView.text = SimpleDateFormat("HH:mm").format(cal.time)
        }
        TimePickerDialog(context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
    }

    fun showBarcodeDialog(
        activity: Activity,
        cancelable: Boolean,
        barcode: String,
    ) : Dialog {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogBarcodeBinding.inflate(LayoutInflater.from(activity))
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(cancelable)

//        val bitmap = encodeAsBitmap(barcode, BarcodeFormat.CODE_128, 600, 300)
        val qrEncoder = BarcodeEncoder()
        val bitmap = qrEncoder.encodeBitmap(barcode, BarcodeFormat.QR_CODE, 600, 600)

        binding.imgBarcode.setImageBitmap(bitmap)
        binding.lblBarcode.text = barcode
        binding.btnConfirmationPositive.setOnSafeClickListener {
            dialog.dismiss()
        }
        binding.imgClose.setOnSafeClickListener {
            dialog.dismiss()
        }


        return dialog
    }

    private fun encodeAsBitmap(
        contents: String?,
        format: BarcodeFormat?,
        img_width: Int,
        img_height: Int
    ): Bitmap? {
        val contentsToEncode = contents ?: return null
        var hints: MutableMap<EncodeHintType?, Any?>? = null
        val encoding: String = guessAppropriateEncoding(contentsToEncode)
        if (encoding != null) {
            hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
            hints!![EncodeHintType.CHARACTER_SET] = encoding
        }
        val writer = MultiFormatWriter()
        val result: BitMatrix
        result = try {
            writer.encode(contentsToEncode, format, img_width, img_height, hints)
        } catch (iae: IllegalArgumentException) {
            // Unsupported format
            return null
        }
        val width = result.width
        val height = result.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (result[x, y]) BLACK else WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(
            width, height,
            Bitmap.Config.ARGB_8888
        )
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    private fun guessAppropriateEncoding(contents: CharSequence): String {
        // Very crude at the moment
        for (i in 0 until contents.length) {
            if (contents[i].toInt() > 0xFF) {
                return "UTF-8"
            }
        }
        return ""
    }
}