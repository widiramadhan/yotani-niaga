package com.javaindoku.yotaniniaga.view.sendstocksupplyfarmer

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseFragment
import com.javaindoku.yotaniniaga.databinding.FragmentSendStockSupplyBinding
import com.javaindoku.yotaniniaga.databinding.FragmentSendStockSupplyFarmerBinding
import com.javaindoku.yotaniniaga.model.gardenPetani.DocumentGarden
import com.javaindoku.yotaniniaga.model.sendstocksupplyfarmer.DataConfirmReservasi
import com.javaindoku.yotaniniaga.model.sendstocksupplyfarmer.SendStockSupplyFarmerBody
import com.javaindoku.yotaniniaga.model.transactionfarmer.TransactionFarmer
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.utilities.SharedPrefKeys
import com.javaindoku.yotaniniaga.utilities.StringFormat
import com.javaindoku.yotaniniaga.utilities.dialog.CustomDialog
import com.javaindoku.yotaniniaga.utilities.extension.findNavController
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import com.javaindoku.yotaniniaga.view.delivery.dialog.AddDocumentDialog
import com.javaindoku.yotaniniaga.view.login.LoginActivity
import com.javaindoku.yotaniniaga.view.notification.NotificationActivity
import com.javaindoku.yotaniniaga.view.sendstocksupplyfarmer.dialog.ConfirmReservasiDialog
import com.javaindoku.yotaniniaga.viewmodel.SendStockSupplyFarmerViewModel
import kotlinx.android.synthetic.main.fragment_send_stock_supply_farmer.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class SendStockSupplyFarmerFragment : BaseFragment() {
    private lateinit var binding: FragmentSendStockSupplyFarmerBinding
    private var dateSelected = ""
    private var timeSelected = ""

    @Inject
    lateinit var viewModel: SendStockSupplyFarmerViewModel

    companion object {
        const val FACTORY_SELECTED = "factorySelected"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_send_stock_supply_farmer, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val selectedFactory = arguments?.getParcelable<TransactionFarmer>(FACTORY_SELECTED)
        initToolbar()
        binding.edtSendDate.setOnSafeClickListener {
            showDatePickerDialogSend()
        }

        binding.edtPksName.setText(selectedFactory!!.companyName)


        binding.lblSendDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s!!.isNotEmpty())
                {
                    binding.imgSendDate.setImageResource(R.drawable.ic_close_black_24dp)
                    binding.imgSendDate.setOnClickListener {
                        binding.lblSendDate.setText("")
                    }
                }
                else
                {
                    binding.imgSendDate.setImageResource(R.drawable.kalender)
                    binding.imgSendDate.setOnClickListener {
                        showDatePickerDialogSend()
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        binding.edtSendTime.setOnSafeClickListener {
            showTimePickerDialogSend()
        }

        binding.lblSendTime.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s!!.isNotEmpty())
                {
                    binding.imgSendTime.setImageResource(R.drawable.ic_close_black_24dp)
                    binding.imgSendTime.setOnClickListener {
                        binding.lblSendTime.setText("")
                    }
                }
                else
                {
                    binding.imgSendTime.setImageResource(R.drawable.ic_arrow_down)
                    binding.imgSendTime.setOnClickListener {
                        showTimePickerDialogSend()
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        binding.btnConfirmationPositive.setOnSafeClickListener {
            if (!isValid()) {
            val sendStockSupplyFarmerBody = SendStockSupplyFarmerBody(
                    pabrikId = selectedFactory.parentPabrikId,
                    petaniId = prefHelper.getStringFromShared(SharedPrefKeys.PETANI_ID)!!,
                    koperasiId = prefHelper.getStringFromShared(SharedPrefKeys.KOPERASI_ID)!!,
                    tanggalPengiriman = StringFormat.dateFormatLaravel("$dateSelected $timeSelected") ,
                    tonasi = StringFormat.getValueFromCurrencyText(binding.edtTonnageEstimationTBS.text.toString()),
                    userId = prefHelper.getStringFromShared(SharedPrefKeys.USER_ID)!!
                )
                viewModel.sendStock(sendStockSupplyFarmerBody, prefHelper.getStringFromShared(SharedPrefKeys.TOKEN)!!)
            }
        }
        observeViewModel(selectedFactory)
    }

    private fun isValid(): Boolean {
        var isError = false
        if (binding.lblSendDate.text.toString().isNullOrBlank()) {
            binding.edtSendDate.background = requireContext().getDrawable(R.drawable.shape_text_input_error)
            binding.sendDateError.visibility = View.VISIBLE
            isError = true
        } else {
            binding.edtSendDate.background = requireContext().getDrawable(R.drawable.shape_text_input)
            binding.sendDateError.visibility = View.GONE
        }

        if (binding.lblSendTime.text.toString().isNullOrBlank()) {
            binding.edtSendTime.background = requireContext().getDrawable(R.drawable.shape_text_input_error)
            binding.sendTimeError.visibility = View.VISIBLE
            isError = true
        } else {
            binding.edtSendTime.background = requireContext().getDrawable(R.drawable.shape_text_input)
            binding.sendTimeError.visibility = View.GONE
        }

        if (binding.edtTonnageEstimationTBS.text.toString().isNullOrBlank()) {
            binding.edtTonnageEstimationTBS.background = requireContext().getDrawable(R.drawable.shape_text_input_error)
            binding.tonasiError.visibility = View.VISIBLE
            isError = true
        } else {
            binding.edtTonnageEstimationTBS.background = requireContext().getDrawable(R.drawable.shape_text_input)
            binding.tonasiError.visibility = View.GONE
        }

        return isError
    }

    private fun observeViewModel(_selectedFactory: TransactionFarmer) {
        val loadingDialog = loadingDialog(requireActivity())
        viewModel.sendStockResult.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(requireContext(), getString(it.messageInt!!), Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                if(it.data?.isSuccess == true){
                    CustomDialog.dialogOneButton(
                        requireActivity(),
                        true,
                        getString(R.string.srTitleSuccess),
                        getString(R.string.sending_stock),
                        R.drawable.ic_dialog_success,
                        true,
                        getString(R.string.action_sign_in),
                        {
                            findNavController().previousBackStackEntry?.savedStateHandle?.set("isSend", true)
                            requireActivity().onBackPressed()
                        }
                    ).show()
                }else{
                    Toast.makeText(requireContext(), it.data?.message.toString(), Toast.LENGTH_SHORT).show()
                    var confimrList = it.data!!.data.toMutableList()
                    if (confimrList.size > 0){
                        val dialog = ConfirmReservasiDialog.listTransaksi(
                            requireActivity(),
                            cancelable = true,
                            labelPositiveButton = getString(R.string.srTxtBtnConfirm),
                            labelNegativeButton = getString(R.string.srTxtBtnCancel),
                            positiveAction = {
                                if(binding.lblSendDate.text.toString().isNullOrBlank()) {
                                    Toast.makeText(requireContext(), getString(R.string.please_fill_date), Toast.LENGTH_SHORT).show()
                                }
                                else if(binding.lblSendTime.text.toString().isNullOrBlank()) {
                                    Toast.makeText(requireContext(), getString(R.string.please_fill_time), Toast.LENGTH_SHORT).show()
                                } else if (binding.edtTonnageEstimationTBS.text.toString().isNullOrBlank()) {
                                    Toast.makeText(requireContext(), getString(R.string.please_fill_tonage_estimation), Toast.LENGTH_SHORT).show()
                                } else {
                                    val sendStockSupplyFarmerBody = SendStockSupplyFarmerBody(
                                        pabrikId = _selectedFactory.parentPabrikId,
                                        petaniId = prefHelper.getStringFromShared(SharedPrefKeys.PETANI_ID)!!,
                                        koperasiId = prefHelper.getStringFromShared(SharedPrefKeys.KOPERASI_ID)!!,
                                        tanggalPengiriman = StringFormat.dateFormatLaravel("$dateSelected $timeSelected") ,
                                        tonasi = StringFormat.getValueFromCurrencyText(binding.edtTonnageEstimationTBS.text.toString()),
                                        userId = prefHelper.getStringFromShared(SharedPrefKeys.USER_ID)!!
                                    )
                                    viewModel.confirmStock(sendStockSupplyFarmerBody, prefHelper.getStringFromShared(SharedPrefKeys.TOKEN)!!)
                                }
                            },
                            negativeAction = {},
                            data = confimrList
                        )
                        dialog.show()
                    }


                }
            }
        })

        viewModel.confirmStockResult.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(requireContext(), getString(it.messageInt!!), Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                if(it.data?.isSuccess == true){
                    CustomDialog.dialogOneButton(
                        requireActivity(),
                        true,
                        getString(R.string.srTitleSuccess),
                        getString(R.string.sending_stock),
                        R.drawable.ic_dialog_success,
                        true,
                        getString(R.string.action_sign_in),
                        {
                            findNavController().previousBackStackEntry?.savedStateHandle?.set("isSend", true)
                            requireActivity().onBackPressed()
                        }
                    ).show()
                }else{
                    Toast.makeText(requireContext(), it.data?.message.toString(), Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun showTimePickerDialogSend() {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)

            timeSelected = SimpleDateFormat("HH:mm").format(cal.time)
            binding.lblSendTime.text = timeSelected
        }
        TimePickerDialog(context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
    }

    private fun showDatePickerDialogSend() {
        val c: Calendar = Calendar.getInstance()
        val mYear: Int = c.get(Calendar.YEAR)
        val mMonth: Int = c.get(Calendar.MONTH)
        val mDay: Int = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = requireActivity().let {
            DatePickerDialog(
                it,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    c.set(Calendar.YEAR, year)
                    c.set(Calendar.MONTH, monthOfYear)
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val myFormat = "dd/MM/yyyy" //In which you need put here
                    val sdf = SimpleDateFormat(myFormat)
                    dateSelected = sdf.format(c.time)
                    binding.lblSendDate.text = dateSelected
                }, mYear, mMonth, mDay
            )
        }
        datePickerDialog.show()
    }

    private fun initToolbar() {
        binding.appbarLayout.lblTitleToolbarHome.text = getString(R.string.send_stock_supply)
        binding.appbarLayout.imgBackToolbarHome.visibility = View.VISIBLE
        binding.appbarLayout.lytNotificationToolbarHome.visibility = View.VISIBLE
        binding.appbarLayout.imgSearchToolbarHome.visibility = View.GONE
        binding.appbarLayout.imgBackToolbarHome.setOnSafeClickListener {
            activity?.onBackPressed()
        }
        binding.appbarLayout.lytNotificationToolbarHome.setOnSafeClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }
    }

}