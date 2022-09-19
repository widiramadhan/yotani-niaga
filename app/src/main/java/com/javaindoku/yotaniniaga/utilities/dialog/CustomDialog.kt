package com.javaindoku.yotaniniaga.utilities.dialog

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginLeft
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.databinding.CustomAlertDialogBinding
import com.javaindoku.yotaniniaga.databinding.DialogAlertButtonWithImageBinding
import com.javaindoku.yotaniniaga.databinding.DialogLoadingBinding

object CustomDialog {
    fun showLoadingDialog(
        activity: Activity,
        cancelable: Boolean
    ): Dialog {

        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogLoadingBinding.inflate(LayoutInflater.from(activity))
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(cancelable)

        Glide.with(activity).asGif().load(R.raw.ic_busy_loading).into(binding.imgGif)

        return dialog
    }

    fun customDialogTwoButton(
        activity: Activity,
        cancelable: Boolean,
        title: String,
        message: String,
        labelPositiveButton: String? = null,
        labelNegativeButton: String? = null,
        positiveAction: ((activity: Activity) -> Unit)? = null,
        negativeAction: ((activity: Activity) -> Unit)? = null,
        username: String? = null,
        isNegative: Boolean = false
    ) : Dialog {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = CustomAlertDialogBinding.inflate(LayoutInflater.from(activity))
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(cancelable)

        binding.lblMessage.text = message
        binding.lblTitle.text = title
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

        if(isNegative) {
            binding.lblTitle.setTextColor(activity.resources.getColor(R.color.colorFailedRed))
            binding.lblUsername.text = username
            binding.lblUsername.visibility = View.VISIBLE
            binding.imgDialog.setImageResource(R.drawable.ic_dialog_failed)
        }

        return dialog
    }

    fun dialogOneButton(
        activity: Activity,
        cancelable: Boolean,
        title: String,
        message: String,
        imageId: Int,
        isPositive: Boolean,
        labelPositiveButton: String? = null,
        positiveAction: ((activity: Activity) -> Unit)? = null,
    ) : Dialog {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogAlertButtonWithImageBinding.inflate(LayoutInflater.from(activity))
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(cancelable)

        binding.lblMessage.text = message
        binding.lblTitle.text = title
        binding.imgStatus.setImageResource(imageId)
        if(isPositive)
        {
            binding.lblTitle.setTextColor(activity.resources.getColor(R.color.colorSuccessGreen))
        }
        else {
            binding.lblTitle.setTextColor(activity.resources.getColor(R.color.colorFailedRed))
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

        return dialog
    }

}
enum class StateDialog{
    WARNING,
    ERROR,
    SUCCESS
}