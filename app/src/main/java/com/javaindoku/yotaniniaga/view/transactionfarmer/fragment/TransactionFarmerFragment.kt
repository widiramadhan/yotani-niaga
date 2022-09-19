package com.javaindoku.yotaniniaga.view.transactionfarmer.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseFragment
import com.javaindoku.yotaniniaga.databinding.FragmentTransactionFarmerBinding
import com.javaindoku.yotaniniaga.di.factory.ViewModelFactory
import com.javaindoku.yotaniniaga.model.checkGarden.CheckGardenFarmerRequest
import com.javaindoku.yotaniniaga.model.checkGarden.CheckGardenKoperasiRequest
import com.javaindoku.yotaniniaga.model.factory.FactoryBody
import com.javaindoku.yotaniniaga.model.transactionfarmer.TransactionFarmer
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.service.network.Status
import com.javaindoku.yotaniniaga.utilities.ConstValue
import com.javaindoku.yotaniniaga.utilities.SharedPrefKeys
import com.javaindoku.yotaniniaga.utilities.dialog.CustomDialog
import com.javaindoku.yotaniniaga.utilities.extension.findNavController
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import com.javaindoku.yotaniniaga.utilities.widget.SpinnerWithSearch
import com.javaindoku.yotaniniaga.view.notification.NotificationActivity
import com.javaindoku.yotaniniaga.view.sendstocksupplyfarmer.SendStockSupplyFarmerFragment
import com.javaindoku.yotaniniaga.view.transactionfarmer.adapter.TransactionFarmerAdapter
import com.javaindoku.yotaniniaga.viewmodel.TransactionFarmerViewModel
import javax.inject.Inject

class TransactionFarmerFragment : BaseFragment() {
    private lateinit var binding: FragmentTransactionFarmerBinding

    @Inject
    lateinit var viewModel: TransactionFarmerViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var isSortingClicked = false

    private var isHighestPriceSelected = false
    private var isNearestSelected = false
    private var isAvailableQuotaSelected = false
    private var isFarthestSelected = false
    private var isLowestPriceSelected = false

    lateinit var transactionFarmer: TransactionFarmer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_transaction_farmer, container, false
        )
        return binding.root
    }

    private var minDistance = "0"
    private var maxDistance = "1000000000000"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.run {
            ViewModelProvider(this, viewModelFactory).get(
                TransactionFarmerViewModel::class.java
            )
        } ?: throw Exception("Invalid Activity")

        initToolbar()
        observe()
        getDataTransaction("jarak_pabrik", "desc", minDistance, maxDistance)
        loadFilter()

        binding.swpHomeFragment.setOnRefreshListener {
            binding.swpHomeFragment.isRefreshing = false
            getDataTransaction("jarak_pabrik", "desc", minDistance, maxDistance)
        }

        binding.rcyTransaction.layoutManager = LinearLayoutManager(requireContext())
        binding.fabScrollUp.setOnSafeClickListener {
            binding.rcyTransaction.smoothScrollToPosition(0)
        }
        binding.lytSorting.lytSorting.setOnClickListener {
            toggleSortButton()
        }
        binding.lytHighestPrice.setOnClickListener {
            toggleHighestPrice(!isHighestPriceSelected)
            toggleSortButton()
        }

        binding.lytNearestDistance.setOnClickListener {
            toggleNearest(!isNearestSelected)
            toggleSortButton()
        }

        binding.lytAvailableQuota.setOnClickListener {
            toggleAvailableQuota(!isAvailableQuotaSelected)
            toggleSortButton()
        }

        binding.lytFarthestDistance.setOnClickListener {
            toggleFarthest(!isFarthestSelected)
            toggleSortButton()
        }

        binding.lytLowestPrice.setOnClickListener {
            toggleLowestPrice(!isLowestPriceSelected)
            toggleSortButton()
        }
    }

    private fun toggleSortButton() {
        if (isSortingClicked) {
            isSortingClicked = false
            binding.crdDetailSorting.visibility = View.GONE
            binding.lytSorting.lytSorting.background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.shape_three,
                null
            )
            binding.lytSorting.imgSorting.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorAccent
                )
            )
            binding.lytSorting.lblSorting.setTextColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.colorAccent,
                    null
                )
            )
        } else {
            isSortingClicked = true
            binding.lytSorting.lytSorting.background = ResourcesCompat.getDrawable(
                resources,
                R.color.colorAccent,
                null
            )
            binding.lytSorting.imgSorting.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            binding.lytSorting.lblSorting.setTextColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.white,
                    null
                )
            )
            showPopUpSortingMenu()
        }
    }

    private fun getDataTransaction(
        orderBy: String = "",
        sort: String = "",
        jarakMinimum: String = "",
        jarakMaksimum: String = ""
    ) {
        val latitude = prefHelper.getStringFromShared(SharedPrefKeys.LATITUDE)
        if (latitude.isNullOrBlank()) {
            binding.lytCompleteProfile.visibility = View.VISIBLE
            binding.rcyTransaction.visibility = View.GONE
        } else {
            loadTransaction(orderBy, sort, jarakMinimum, jarakMaksimum)
            binding.lytCompleteProfile.visibility = View.GONE
            binding.rcyTransaction.visibility = View.VISIBLE
        }
    }

    private fun setFilterLayoutDisable() {
        isSortingClicked = false
        binding.lytSorting.lytSorting.background = ResourcesCompat.getDrawable(
            resources,
            R.drawable.shape_three,
            null
        )
        binding.lytSorting.imgSorting.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorAccent
            )
        )
        binding.lytSorting.lblSorting.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.colorAccent,
                null
            )
        )
    }

    private fun showPopUpSortingMenu() {
        binding.lytHideSorting.visibility = View.VISIBLE
        binding.lytHideSorting.setOnClickListener {
            binding.lytHideSorting.visibility = View.GONE
            binding.crdDetailSorting.visibility = View.GONE
            setFilterLayoutDisable()
        }
        binding.crdDetailSorting.visibility = View.VISIBLE
    }

    private fun toggleHighestPrice(activate: Boolean) {
        if (activate) {
            isLowestPriceSelected = false
            isAvailableQuotaSelected = false
            isNearestSelected = false
            isFarthestSelected = false
            isHighestPriceSelected = true
            binding.lblHighestPrice.setTextColor(resources.getColor(R.color.colorPrimary))
            binding.lblFarthestDistance.setTextColor(resources.getColor(R.color.color_title_home))
            binding.lblAvailableQuota.setTextColor(resources.getColor(R.color.color_title_home))
            binding.lblNearestDistance.setTextColor(resources.getColor(R.color.color_title_home))
            binding.lblLowestPrice.setTextColor(resources.getColor(R.color.color_title_home))
            getDataTransaction("harga", "desc", minDistance, maxDistance)
            return
        }
        isHighestPriceSelected = false
        binding.lblHighestPrice.setTextColor(resources.getColor(R.color.color_title_home))
        getDataTransaction("jarak_pabrik", "desc", minDistance, maxDistance)
    }

    private fun toggleLowestPrice(activate: Boolean) {
        if (activate) {
            isLowestPriceSelected = true
            isHighestPriceSelected = false
            isAvailableQuotaSelected = false
            isNearestSelected = false
            isFarthestSelected = false
            binding.lblHighestPrice.setTextColor(resources.getColor(R.color.color_title_home))
            binding.lblFarthestDistance.setTextColor(resources.getColor(R.color.color_title_home))
            binding.lblAvailableQuota.setTextColor(resources.getColor(R.color.color_title_home))
            binding.lblNearestDistance.setTextColor(resources.getColor(R.color.color_title_home))
            binding.lblLowestPrice.setTextColor(resources.getColor(R.color.colorPrimary))
            getDataTransaction("harga", "asc", minDistance, maxDistance)
            return
        }
        isLowestPriceSelected = false
        binding.lblLowestPrice.setTextColor(resources.getColor(R.color.color_title_home))
        getDataTransaction("jarak_pabrik", "asc", minDistance, maxDistance)
    }

    private fun toggleAvailableQuota(activate: Boolean) {
        if (activate) {
            isLowestPriceSelected = false
            isAvailableQuotaSelected = true
            isNearestSelected = false
            isFarthestSelected = false
            isHighestPriceSelected = false
            binding.lblHighestPrice.setTextColor(resources.getColor(R.color.color_title_home))
            binding.lblFarthestDistance.setTextColor(resources.getColor(R.color.color_title_home))
            binding.lblAvailableQuota.setTextColor(resources.getColor(R.color.colorPrimary))
            binding.lblNearestDistance.setTextColor(resources.getColor(R.color.color_title_home))
            binding.lblLowestPrice.setTextColor(resources.getColor(R.color.color_title_home))
            getDataTransaction("kuota", "desc", minDistance, maxDistance)
            return
        }
        isAvailableQuotaSelected = false
        binding.lblAvailableQuota.setTextColor(resources.getColor(R.color.color_title_home))
        getDataTransaction("jarak_pabrik", "asc", minDistance, maxDistance)
    }

    private fun toggleNearest(activate: Boolean) {
        if (activate) {
            isLowestPriceSelected = false
            isAvailableQuotaSelected = false
            isNearestSelected = true
            isFarthestSelected = false
            isHighestPriceSelected = false
            binding.lblHighestPrice.setTextColor(resources.getColor(R.color.color_title_home))
            binding.lblFarthestDistance.setTextColor(resources.getColor(R.color.color_title_home))
            binding.lblAvailableQuota.setTextColor(resources.getColor(R.color.color_title_home))
            binding.lblNearestDistance.setTextColor(resources.getColor(R.color.colorPrimary))
            binding.lblLowestPrice.setTextColor(resources.getColor(R.color.color_title_home))
            getDataTransaction("jarak_pabrik", "asc", minDistance, maxDistance)
            return
        }
        isNearestSelected = false
        binding.lblNearestDistance.setTextColor(resources.getColor(R.color.color_title_home))
        getDataTransaction("jarak_pabrik", "asc", minDistance, maxDistance)
    }

    private fun toggleFarthest(activate: Boolean) {
        if (activate) {
            isLowestPriceSelected = false
            isAvailableQuotaSelected = false
            isNearestSelected = false
            isFarthestSelected = true
            isHighestPriceSelected = false
            binding.lblHighestPrice.setTextColor(resources.getColor(R.color.color_title_home))
            binding.lblFarthestDistance.setTextColor(resources.getColor(R.color.colorPrimary))
            binding.lblAvailableQuota.setTextColor(resources.getColor(R.color.color_title_home))
            binding.lblNearestDistance.setTextColor(resources.getColor(R.color.color_title_home))
            binding.lblLowestPrice.setTextColor(resources.getColor(R.color.color_title_home))
            getDataTransaction("jarak_pabrik", "desc", minDistance, maxDistance)
            return
        }
        isFarthestSelected = false
        binding.lblFarthestDistance.setTextColor(resources.getColor(R.color.color_title_home))
        getDataTransaction("jarak_pabrik", "asc", minDistance, maxDistance)
    }

    private fun initToolbar() {
        binding.appbarLayout.lblTitleToolbarHome.text = getString(R.string.list_pks_title)
        binding.appbarLayout.imgBackToolbarHome.visibility = View.GONE
        binding.appbarLayout.lytNotificationToolbarHome.visibility = View.VISIBLE
        binding.appbarLayout.imgSearchToolbarHome.visibility = View.GONE
        binding.appbarLayout.imgBackToolbarHome.setOnSafeClickListener {
            activity?.onBackPressed()
        }
        binding.appbarLayout.lytNotificationToolbarHome.setOnSafeClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }
    }

    private fun loadFilter() {
        val dataListString = mutableListOf<String>()
        dataListString.add("Any")
        dataListString.add("Radius 0 - 50 km")
        dataListString.add("Radius 50 - 75 km")
        dataListString.add("Radius 75 - 100 km")
        binding.spnSrcFilterDistance.setList(dataListString)
        binding.spnSrcFilterDistance.getSelectedItem(object : SpinnerWithSearch.OnItemSelected {
            override fun onItemSelected(text: String, position: Int) {
                when (position) {
                    0 -> {
                        minDistance = "0"
                        maxDistance = "1000000000000"
                        getDataTransaction("jarak_pabrik", "desc", minDistance, maxDistance)
                    }
                    1 -> {
                        minDistance = "0"
                        maxDistance = "50000"
                        getDataTransaction("jarak_pabrik", "desc", minDistance, maxDistance)
                    }
                    2 -> {
                        minDistance = "50001"
                        maxDistance = "75000"
                        getDataTransaction("jarak_pabrik", "desc", minDistance, maxDistance)
                    }
                    else -> {
                        minDistance = "750001"
                        maxDistance = "100000"
                        getDataTransaction("jarak_pabrik", "desc", minDistance, maxDistance)
                    }
                }
            }
        })
    }

    private fun loadTransaction(
        orderBy: String = "",
        sort: String = "",
        jarakMinimum: String = "",
        jarakMaksimum: String = ""
    ) {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("isSend")!!
            .observe(viewLifecycleOwner, Observer {
                if (it) {
                    viewModel.retry()
                }
            })
        requireActivity().let {
            viewModel.pageInit.postValue(0)
            viewModel.token = prefHelper.getStringFromShared(SharedPrefKeys.TOKEN).toString()
            viewModel.factoryBody = FactoryBody(
                user_id = prefHelper.getStringFromShared(SharedPrefKeys.USER_ID)!!,
                orderBy = orderBy, //jarak_pabrik
                sort = sort, //desc
                latitude = "20.149", //prefHelper.getStringFromShared(SharedPrefKeys.LATITUDE)!!,
                longitude = "77.49", //prefHelper.getStringFromShared(SharedPrefKeys.LONGITUDE)!!,
                jarak_minimum = jarakMinimum,//0
                jarak_maksimum = jarakMaksimum, //1000000
            )

            val adapter = TransactionFarmerAdapter({ viewModel.retry() },
                requireActivity(), object : TransactionFarmerAdapter.TransactionFarmerInterface {
                    override fun toSendStockSupply(transaction: TransactionFarmer) {
                        transactionFarmer = transaction
                        //validationCheckGarden()
                        val bundle = Bundle()
                        bundle.putParcelable(SendStockSupplyFarmerFragment.FACTORY_SELECTED, transaction)
                        findNavController().navigate(R.id.actionTransactionFragmentToSendStockSupplyFarmerFragment, bundle)
                    }
                }
            )

            viewModel.pos.observe(viewLifecycleOwner, Observer {
                adapter.submitList(it) {
                    val layoutManager = binding.rcyTransaction.layoutManager as LinearLayoutManager
                    val position = layoutManager.findFirstCompletelyVisibleItemPosition()
                    if (position != RecyclerView.NO_POSITION) {
                        binding.rcyTransaction.scrollToPosition(position)
                    }
                }
            })

            viewModel.networkState.observe(viewLifecycleOwner, Observer {
                adapter.setNetworkState(it)
                if (it.status == Status.FAILED) {

                } else if (it.status == Status.FAILED) {

                } else if (it.status == Status.RUNNING) {

                } else if (it.status == Status.SUCCESS) {

                }
            })

            binding.rcyTransaction.adapter = adapter
            binding.rcyTransaction.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )

            var totalScrollYUp = viewModel.totalScrollYRecyclerView

            binding.rcyTransaction.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
    }

    private fun validationCheckGarden() {
        val typeUser = prefHelper.getStringFromShared(SharedPrefKeys.TYPE_USER)!!
        if (typeUser == ConstValue.KOPERASI){
            val checkGardenRequest = CheckGardenKoperasiRequest(
                user_id = prefHelper.getStringFromShared(SharedPrefKeys.USER_ID)!!,
                koperasi_id = prefHelper.getStringFromShared(SharedPrefKeys.KOPERASI_ID)!!
            )
            viewModel.checkGardenKoperasi(checkGardenRequest, prefHelper.getStringFromShared(SharedPrefKeys.TOKEN)!!)
        }else{
            val checkGardenRequest = CheckGardenFarmerRequest(
                user_id = prefHelper.getStringFromShared(SharedPrefKeys.USER_ID)!!,
                petani_id = prefHelper.getStringFromShared(SharedPrefKeys.PETANI_ID)!!
            )
            viewModel.checkGardenFarmer(checkGardenRequest, prefHelper.getStringFromShared(SharedPrefKeys.TOKEN)!!)
        }
    }

    fun observe() {
        val loadingDialog = loadingDialog(requireActivity())
        viewModel.checkGardenResult.observe(requireActivity(), androidx.lifecycle.Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(requireContext(), getString(it.messageInt!!), Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                if(it.data?.isSuccess == true){
                    Log.e("cekObserve","observe")
                    if (it.data?.data?.checkKebunKoperasi == false){
                        CustomDialog.dialogOneButton(
                            requireActivity(),
                            false,
                            "Tidak bisa Reservasi",
                            "Silahkan tambah Data Petani dan Kebun terlebih dahulu",
                            R.drawable.ic_dialog_success,
                            true,
                            "Ok", {}
                        ).show()
                    }else{
                        val bundle = Bundle()
                        bundle.putParcelable(SendStockSupplyFarmerFragment.FACTORY_SELECTED, transactionFarmer)
                        findNavController().navigate(R.id.actionTransactionFragmentToSendStockSupplyFarmerFragment, bundle)
                    }
                }else{
                    Toast.makeText(requireContext(), it.data?.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

}