package com.javaindoku.yotaniniaga.view.delivery

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseFragment
import com.javaindoku.yotaniniaga.databinding.FragmentDeliveryBinding
import com.javaindoku.yotaniniaga.model.delivery.FilterStatus
import com.javaindoku.yotaniniaga.model.deliveryreservation.DeliveryData
import com.javaindoku.yotaniniaga.model.invoice.Invoice
import com.javaindoku.yotaniniaga.model.invoiceDetail.InvoiceDetail
import com.javaindoku.yotaniniaga.model.invoiceDetail.InvoiceDetailData
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.service.network.Status
import com.javaindoku.yotaniniaga.utilities.ConstValue
import com.javaindoku.yotaniniaga.utilities.SharedPrefKeys
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import com.javaindoku.yotaniniaga.view.delivery.adapter.DeliveryKoperasiAdapter
import com.javaindoku.yotaniniaga.view.delivery.dialog.DeliveryDialog
import com.javaindoku.yotaniniaga.view.invoice.dialog.InvoiceDialog
import com.javaindoku.yotaniniaga.view.notification.NotificationActivity
import com.javaindoku.yotaniniaga.viewmodel.DeliveryKoperasiViewModel
import dagger.android.AndroidInjection
import javax.inject.Inject

class DeliveryFragment : BaseFragment() {
    private lateinit var binding: FragmentDeliveryBinding

    private var isLatestDateSelected = false
    private var isOldestDateSelected = false
    private var isOnDeliverySelected = false
    private var isOnProcessSelected = false
    private lateinit var listFilter: List<FilterStatus>
    private var sort = ConstValue.DESCENDING
    private var selectedStatus = ""

    @Inject
    lateinit var viewModel: DeliveryKoperasiViewModel

    private lateinit var rescheduleDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AndroidInjection.inject(requireActivity())
        binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_delivery, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        observeViewModel()
        binding.rcyDelivery.layoutManager = LinearLayoutManager(requireContext())
        loadDelivery(sort,"")
        binding.fabScrollUp.setOnSafeClickListener {
            binding.rcyDelivery.smoothScrollToPosition(0)
        }
        binding.lytSorting.lytSorting.setOnClickListener {
            if(binding.crdDetailSorting.visibility == View.VISIBLE) {
                toggleSorting(false)
            }
            else{
                toggleSorting(true)
                showPopUpSortingMenu()
            }
        }
        binding.lytSorting.lytFilter.setOnClickListener {
            if(binding.crdDetailFilter.visibility == View.VISIBLE) {
                toggleFilter(false)
            }
            else{
                toggleFilter(true)
                showPopUpFilterMenu()
            }
        }
        binding.lytHideSorting.setOnClickListener {
            binding.lytHideSorting.visibility = View.GONE
            binding.crdDetailSorting.visibility = View.INVISIBLE
            binding.crdDetailFilter.visibility = View.INVISIBLE
        }

        binding.swpHomeFragment.setOnRefreshListener {
            binding.swpHomeFragment.isRefreshing = false
            activity.let {
                loadDelivery(sort,"")
            }
        }

        binding.lytOnProcess.setOnClickListener{view ->
            if (!isOnProcessSelected) {
                binding.lblOnDelivery.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_title_home))
                binding.lblOnProcess.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                selectedStatus = "2"
                loadDelivery(sort, selectedStatus)
            } else {
                binding.lblOnProcess.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_title_home))
                selectedStatus = ""
                loadDelivery(sort, selectedStatus)
            }
            isOnProcessSelected = !isOnProcessSelected
            toggleFilter(false)
        }

        binding.lytOnDelivery.setOnClickListener{ view ->
            if (!isOnDeliverySelected) {
                binding.lblOnDelivery.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                binding.lblOnProcess.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_title_home))
                selectedStatus = "1"
                loadDelivery(sort, selectedStatus)
            } else {
                binding.lblOnDelivery.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_title_home))
                selectedStatus = ""
                loadDelivery(sort, selectedStatus)
            }
            isOnDeliverySelected = !isOnDeliverySelected
            toggleFilter(false)
        }

        binding.lytLatestDate.setOnClickListener {
            binding.crdDetailSorting.visibility = View.INVISIBLE
            if(isLatestDateSelected) {
                isLatestDateSelected = false
                binding.lblLatestDate.setTextColor(resources.getColor(R.color.color_title_home))
                sort = ConstValue.DESCENDING
                loadDelivery(sort, selectedStatus)
            }
            else {
                isLatestDateSelected = true
                isOldestDateSelected = false
                binding.lblOldestDate.setTextColor(resources.getColor(R.color.color_title_home))
                binding.lblLatestDate.setTextColor(resources.getColor(R.color.colorPrimary))
                sort = ConstValue.DESCENDING
                loadDelivery(sort, selectedStatus)
            }
            toggleSorting(false)
        }

        binding.lytOldestDate.setOnClickListener {
            binding.crdDetailSorting.visibility = View.INVISIBLE
            if(isOldestDateSelected) {
                isOldestDateSelected = false
                binding.lblOldestDate.setTextColor(resources.getColor(R.color.color_title_home))
                sort = ConstValue.DESCENDING
                loadDelivery(sort, selectedStatus)
            }
            else {
                isOldestDateSelected = true
                isLatestDateSelected = false
                binding.lblLatestDate.setTextColor(resources.getColor(R.color.color_title_home))
                binding.lblOldestDate.setTextColor(resources.getColor(R.color.colorPrimary))
                sort = ConstValue.ASCENDING
                loadDelivery(sort, selectedStatus)
            }
            toggleSorting(false)
        }
    }

    private fun toggleSorting(activate: Boolean) {
        if (activate) {
            binding.lytSorting.lytSorting.background = context?.getDrawable(R.color.colorAccent)
            binding.lytSorting.imgSorting.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            binding.lytSorting.lblSorting.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
        } else {
            binding.lytSorting.lytSorting.background = context?.getDrawable(R.color.white)
            binding.lytSorting.imgSorting.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorAccent
                )
            )
            binding.lytSorting.lblSorting.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorAccent
                )
            )
            binding.crdDetailSorting.visibility = View.INVISIBLE
        }
    }

    private fun toggleFilter(activate: Boolean) {
        if (activate) {
            binding.lytSorting.lytFilter.background = context?.getDrawable(R.color.colorAccent)
            binding.lytSorting.imgFilter.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            binding.lytSorting.lblFilter.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
        } else {
            binding.lytSorting.lytFilter.background = context?.getDrawable(R.color.white)
            binding.lytSorting.imgFilter.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorAccent
                )
            )
            binding.lytSorting.lblFilter.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorAccent
                )
            )
            binding.crdDetailFilter.visibility = View.INVISIBLE
        }
    }

    private fun initToolbar() {
        binding.lytToolbar.lblTitleToolbarHome.text = getString(R.string.list_reservation_tbs_delivery)
        binding.lytToolbar.imgBackToolbarHome.visibility = View.GONE
        binding.lytToolbar.imgSearchToolbarHome.visibility = View.GONE
        binding.lytToolbar.lytNotificationToolbarHome.visibility = View.VISIBLE
        binding.lytToolbar.imgBackToolbarHome.setOnSafeClickListener {
            requireActivity().onBackPressed()
        }
        binding.lytToolbar.lytNotificationToolbarHome.setOnSafeClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }
    }

    private fun showPopUpSortingMenu(){
        binding.lytHideSorting.visibility = View.VISIBLE
        binding.crdDetailSorting.visibility = View.VISIBLE
    }

    private fun showPopUpFilterMenu(){
        binding.lytHideSorting.visibility = View.VISIBLE
        binding.crdDetailFilter.visibility = View.VISIBLE
    }

    private fun loadDelivery (sort: String = "", status : String = "") {
        viewModel.deliveryReservationBody.put("page", 1)
        viewModel.deliveryReservationBody.put("sort", sort)
        viewModel.deliveryReservationBody.put("status", status)
        viewModel.deliveryReservationBody.put("type_user", prefHelper.getStringFromShared(SharedPrefKeys.TYPE_USER).toString())
        viewModel.deliveryReservationBody.put("user_id", prefHelper.getStringFromShared(SharedPrefKeys.USER_ID).toString())
        viewModel.token = prefHelper.getStringFromShared(SharedPrefKeys.TOKEN)!!
        viewModel.pageInit.postValue(0)

        val adapter = DeliveryKoperasiAdapter({viewModel.retry()}, requireActivity(), object : DeliveryKoperasiAdapter.DeliveryKoperasiClick {
            override fun reschedule(deliveryData: DeliveryData) {
                rescheduleDialog = DeliveryDialog.rescheduleDialog (requireActivity(),
                    cancelable = true,
                    labelPositiveButton = getString(R.string.srTxtBtnPositive),
                    labelNegativeButton = getString(R.string.srTxtBtnNegative),
                    positiveAction = {

                    },
                    negativeAction = {

                    },
                    deliveryData = deliveryData,
                    viewModel = viewModel,
                    petaniId = prefHelper.getStringFromShared(SharedPrefKeys.PETANI_ID)!!,
                    userId = prefHelper.getStringFromShared(SharedPrefKeys.USER_ID)!!
                )
                rescheduleDialog.show()
            }

            override fun showBarcode(deliveryKoperasi: DeliveryData) {
                val dialog = DeliveryDialog.showBarcodeDialog(requireActivity(),
                    cancelable = true,
                    barcode = deliveryKoperasi.noReservasi
                )
                dialog.show()
            }

            override fun viewInvoice(deliveryKoperasi: DeliveryData) {
                // VIEW DETAIL INVOICE
                viewModel.invoiceDetail(prefHelper.getStringFromShared(SharedPrefKeys.TOKEN)!!,
                                        prefHelper.getStringFromShared(SharedPrefKeys.USER_ID)!!,
                                        deliveryKoperasi.noInvoice.toString())
            }
        })

        viewModel.pos.observe(requireActivity(), Observer {
            adapter.submitList(it) {
                val layoutManager = binding.rcyDelivery.layoutManager as LinearLayoutManager
                val position = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (position != RecyclerView.NO_POSITION) {
                    binding.rcyDelivery.scrollToPosition(position)
                }
            }
        })

        viewModel.networkState.observe(requireActivity(), Observer {
            val networkState = it
            adapter.setNetworkState(it)
            if (it.status == Status.FAILED)
            {

            }
            else if(it.status == Status.FAILED)
            {

            }
            else if (it.status == Status.RUNNING)
            {

            }
            else if (it.status == Status.SUCCESS)
            {

            }
        })

        binding.rcyDelivery.adapter = adapter
        binding.rcyDelivery.addItemDecoration(
            DividerItemDecoration(requireContext(),
                DividerItemDecoration.VERTICAL)
        )

        var totalScrollYUp = viewModel.totalScrollYRecyclerView
        binding.rcyDelivery.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                totalScrollYUp += dy
                viewModel.totalScrollYRecyclerView = totalScrollYUp

                if (dx == 0 && totalScrollYUp < 200) {
                    if (binding.lytSorting.root.isVisible) {
                        binding.lytSorting.root.visibility = View.GONE
                    }
                } else if (dx >= 0 && totalScrollYUp > 200) {
                    if (!binding.lytSorting.root.isVisible) {
                        binding.lytSorting.root.visibility = View.VISIBLE
                    }
                }

                if (dx == 0 && totalScrollYUp < 200) {
                    if (binding.fabScrollUp.isVisible) {
                        binding.fabScrollUp.visibility = View.GONE
                    }
                } else if (dx >= 0 && totalScrollYUp > 500) {
                    if (!binding.fabScrollUp.isVisible) {
                        binding.fabScrollUp.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun observeViewModel() {
        val loadingDialog = loadingDialog(requireActivity())
        // RESECHEDULE RESERVATION
        viewModel.deliveryReservationEditBody.observe(viewLifecycleOwner, Observer {
            viewModel.rescheduleReservation(prefHelper.getStringFromShared(SharedPrefKeys.TOKEN)!!)
        })
        viewModel.rescheduleResponse.observe(viewLifecycleOwner, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(requireContext(), getString(it.messageInt!!), Toast.LENGTH_SHORT)
                    .show()
            } else {
                loadingDialog.dismiss()
                Toast.makeText(requireContext(), getString(R.string.reschedule_success), Toast.LENGTH_SHORT)
                    .show()
                viewModel.refresh()
                rescheduleDialog.dismiss()
            }
        })

        viewModel.invoiceDetailResult.observe(requireActivity(), Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(requireContext(), getString(it.messageInt!!), Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                val viewDetailDialog = InvoiceDialog.viewDetailApi(requireActivity(),
                    cancelable = true,
                    labelPositiveButton = getString(R.string.srTxtBtnPositive),
                    labelNegativeButton = getString(R.string.srTxtBtnNegative),
                    positiveAction = {},
                    negativeAction = {},
                    invoiceDetailData = it.data!!.data!!,
                    language = prefHelper.getStringFromShared(SharedPrefKeys.LANGUAGE).toString()
                )
                viewDetailDialog.show()
            }
        })
    }

}