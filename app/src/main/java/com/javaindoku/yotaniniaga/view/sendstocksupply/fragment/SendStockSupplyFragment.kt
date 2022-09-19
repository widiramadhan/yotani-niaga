package com.javaindoku.yotaniniaga.view.sendstocksupply.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseFragment
import com.javaindoku.yotaniniaga.databinding.DialogAddPetaniBinding
import com.javaindoku.yotaniniaga.databinding.FragmentSendStockSupplyBinding
import com.javaindoku.yotaniniaga.model.gardenPetani.CropToSend
import com.javaindoku.yotaniniaga.model.profile.Farmer
import com.javaindoku.yotaniniaga.model.transactionfarmer.TransactionFarmer
import com.javaindoku.yotaniniaga.utilities.CustomSpinnerAdapter
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import com.javaindoku.yotaniniaga.view.notification.NotificationActivity
import com.javaindoku.yotaniniaga.view.sendstocksupply.adapter.FarmerGardenListAdapter
import com.javaindoku.yotaniniaga.view.sendstocksupplyfarmer.SendStockSupplyFarmerFragment.Companion.FACTORY_SELECTED
import com.javaindoku.yotaniniaga.viewmodel.SendStockSupplyViewModel
import com.toptoche.searchablespinnerlibrary.SearchableSpinner
import javax.inject.Inject

class SendStockSupplyFragment : BaseFragment(), FarmerGardenListAdapter.ItemMenuInterface,
    AdapterView.OnItemSelectedListener {
    private var editPosition = -1
    private var isEdit = false
    private lateinit var adapter: FarmerGardenListAdapter
    private lateinit var binding: FragmentSendStockSupplyBinding
    @Inject lateinit var viewModel: SendStockSupplyViewModel
    lateinit var petaniAdapter: CustomSpinnerAdapter<Farmer>
    lateinit var kebunAdapter: CustomSpinnerAdapter<CropToSend>
    private var selectedKebunList = ArrayList<CropToSend>()
    lateinit var addPetaniDialog: Dialog
    lateinit var farmerSpinner: SearchableSpinner
    lateinit var gardenSpinner: SearchableSpinner
    lateinit var tonasiEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_send_stock_supply, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        petaniAdapter = CustomSpinnerAdapter(requireContext(), ArrayList())
        kebunAdapter = CustomSpinnerAdapter(requireContext(), ArrayList())
        addPetaniDialog = createDialog()
        val data = arguments?.get(FACTORY_SELECTED) as TransactionFarmer
        binding.edtPksName.setText(data.companyName)
        viewModel.getListPetaniByKoperasi("1")
        binding.recyclerListKebun.layoutManager = LinearLayoutManager(requireContext())
        adapter = FarmerGardenListAdapter(requireContext(), ArrayList(), this)
        binding.recyclerListKebun.adapter = adapter
        binding.layoutAddGarden.setOnClickListener{
            isEdit = false
            tonasiEditText.setText("")
            addPetaniDialog.show()
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.listPetani.observe(requireActivity(), Observer { listPetani ->
            petaniAdapter = CustomSpinnerAdapter(requireContext(), listPetani)
            farmerSpinner.adapter = petaniAdapter
        })
        viewModel.listKebun.observe(requireActivity(), Observer { listKebun ->
            kebunAdapter = CustomSpinnerAdapter(requireContext(), listKebun)
            gardenSpinner.adapter = kebunAdapter
        })
    }

    private fun createDialog() : Dialog {
        val dialog = Dialog(requireContext())
        val viewBinding = DialogAddPetaniBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(viewBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        farmerSpinner = viewBinding.spinnerFarmer
        gardenSpinner = viewBinding.spinnerGarden
        tonasiEditText = viewBinding.edtTotalTonasi
        viewBinding.spinnerFarmer.adapter = petaniAdapter
        viewBinding.spinnerGarden.adapter = kebunAdapter
        viewBinding.btnConfirmationNegative.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorOrange))
        viewBinding.btnConfirmationPositive.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        viewBinding.btnConfirmationNegative.setText(context?.resources?.getString(R.string.srCancel))
        viewBinding.btnConfirmationPositive.setText(context?.resources?.getString(R.string.srAdd))
        viewBinding.btnConfirmationNegative.setOnClickListener { view -> dialog.dismiss() }
        viewBinding.btnConfirmationPositive.setOnClickListener { view ->
            val selectedKebun =
                kebunAdapter.getItemData(viewBinding.spinnerGarden.selectedItemPosition)
            selectedKebun.tonasi = viewBinding.edtTotalTonasi.text.toString().toInt()
            if (isEdit) selectedKebunList[editPosition] = selectedKebun
            else selectedKebunList.add(selectedKebun)
            adapter.refreshItem(selectedKebunList)
            dialog.dismiss()
        }
        viewBinding.spinnerFarmer.onItemSelectedListener = this
        return dialog
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

    override fun onEdit(garden: CropToSend, position: Int) {
        tonasiEditText.setText(garden.tonasi.toString())
        farmerSpinner.setSelection(petaniAdapter.getItemPositionById(garden.id))
        gardenSpinner.setSelection(kebunAdapter.getItemPositionById(garden.petaniId))
        editPosition = position
        isEdit = true
        addPetaniDialog.show()
    }

    override fun onRemove(position: Int) {
        selectedKebunList.removeAt(position)
        adapter.refreshItem(selectedKebunList)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (petaniAdapter.items.isNotEmpty())
            viewModel.getListKebunByPetani(petaniAdapter.getItemData(p2).id)
    }

}