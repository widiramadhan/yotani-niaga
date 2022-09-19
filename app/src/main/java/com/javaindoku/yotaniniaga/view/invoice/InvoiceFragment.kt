package com.javaindoku.yotaniniaga.view.invoice

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseFragment
import com.javaindoku.yotaniniaga.databinding.FragmentInvoiceBinding
import com.javaindoku.yotaniniaga.model.invoice.Invoice
import com.javaindoku.yotaniniaga.model.invoice.InvoiceRequest
import com.javaindoku.yotaniniaga.model.invoice.RequestFilter
import com.javaindoku.yotaniniaga.service.network.Status
import com.javaindoku.yotaniniaga.utilities.ConstValue
import com.javaindoku.yotaniniaga.utilities.SharedPrefKeys
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import com.javaindoku.yotaniniaga.view.invoice.adapter.InvoiceAdapter
import com.javaindoku.yotaniniaga.view.invoice.dialog.InvoiceDialog
import com.javaindoku.yotaniniaga.view.notification.NotificationActivity
import com.javaindoku.yotaniniaga.viewmodel.InvoiceViewModel
import kotlinx.android.synthetic.main.fragment_invoice.*
import javax.inject.Inject

class InvoiceFragment : BaseFragment() {

    private var searchShown = false
    private var sort = ConstValue.ASCENDING
    private var filterPaidToggle = false
    private var filterUnpaidToggle = false
    private val STATUS_UNPAID = "5"
    private val STATUS_PAID = "6"
    private lateinit var binding: FragmentInvoiceBinding
    private lateinit var inflater: MenuInflater

    @Inject
    lateinit var viewModel: InvoiceViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_invoice, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        binding.rcyInvoice.layoutManager = LinearLayoutManager(requireContext())
        loadInvoice()
//        dataFilter()

        binding.swpHomeFragment.setOnRefreshListener {
            binding.swpHomeFragment.isRefreshing = false
            activity.let {
                loadInvoice(binding.edtSearch.text.toString(), sort, createRequestFilter())
            }
        }

        binding.lytSorting.lytSorting.setOnClickListener {
            if(binding.crdDetailSorting.visibility == View.VISIBLE) {
                binding.crdDetailSorting.visibility = View.INVISIBLE
            }
            else{
                showPopUpSortingMenu()
            }
        }
        binding.lytSorting.lytFilter.setOnClickListener {
            if(binding.crdDetailFilter.visibility == View.VISIBLE) {
                binding.crdDetailFilter.visibility = View.INVISIBLE
            }
            else{
                showPopUpFilterMenu()
            }
        }
        binding.fabScrollUp.setOnSafeClickListener {
            binding.rcyInvoice.smoothScrollToPosition(0)
        }
        binding.lytHideSorting.setOnClickListener {
            binding.lytHideSorting.visibility = View.GONE
            binding.crdDetailFilter.visibility = View.INVISIBLE
            binding.crdDetailSorting.visibility = View.INVISIBLE
        }
    }

    private fun createRequestFilter(): RequestFilter? {
        if (filterPaidToggle == true) return RequestFilter(STATUS_PAID)
        else if (filterUnpaidToggle == true) return RequestFilter(STATUS_UNPAID)
        return null
    }

    private fun showPopUpSortingMenu(){
        binding.lytHideSorting.visibility = View.VISIBLE
        binding.crdDetailSorting.visibility = View.VISIBLE
        binding.lytLatestDate.setOnClickListener {
            binding.lblLatestDate.setTextColor(resources.getColor(R.color.colorPrimary))
            binding.lblOldestDate.setTextColor(resources.getColor(R.color.color_title_home))
            sort = ConstValue.DESCENDING
            loadInvoice(binding.edtSearch.text.toString(), sort, createRequestFilter())
            binding.crdDetailSorting.visibility = View.INVISIBLE
        }

        binding.lytOldestDate.setOnClickListener {
            binding.lblLatestDate.setTextColor(resources.getColor(R.color.color_title_home))
            binding.lblOldestDate.setTextColor(resources.getColor(R.color.colorPrimary))
            sort = ConstValue.ASCENDING
            loadInvoice(binding.edtSearch.text.toString(), sort, createRequestFilter())
            binding.crdDetailSorting.visibility = View.INVISIBLE
        }
    }

    private fun showPopUpFilterMenu(){
        binding.lytHideSorting.visibility = View.VISIBLE
        binding.crdDetailFilter.visibility = View.VISIBLE

        binding.lytStatusAlreadyPaid.setOnClickListener {
            binding.crdDetailFilter.visibility = View.INVISIBLE
            toggleFilterPaid()
        }

        binding.lytStatusNotYetPaid.setOnClickListener {
            binding.crdDetailFilter.visibility = View.INVISIBLE
            toggleFilterUnpaid()
        }
    }

    private fun toggleFilterUnpaid() {
        if (filterUnpaidToggle) {
            filterUnpaidToggle = false
            binding.lblStatusNotYetPaid.setTextColor(resources.getColor(R.color.color_title_home))
        } else {
            filterUnpaidToggle = true
            filterPaidToggle = false
            binding.lblStatusAlreadyPaid.setTextColor(resources.getColor(R.color.color_title_home))
            binding.lblStatusNotYetPaid.setTextColor(resources.getColor(R.color.colorPrimary))
        }
        loadInvoice(binding.edtSearch.text.toString(), sort, createRequestFilter())
    }

    private fun toggleFilterPaid() {
        if (filterPaidToggle) {
            filterPaidToggle = false
            binding.lblStatusAlreadyPaid.setTextColor(resources.getColor(R.color.color_title_home))
        } else {
            filterPaidToggle = true
            filterUnpaidToggle = false
            binding.lblStatusAlreadyPaid.setTextColor(resources.getColor(R.color.colorPrimary))
            binding.lblStatusNotYetPaid.setTextColor(resources.getColor(R.color.color_title_home))
        }
        loadInvoice(binding.edtSearch.text.toString(), sort, createRequestFilter())
    }

    private fun loadInvoice (keyword: String = "", sort: String = "asc", filter: RequestFilter? = null) {
        viewModel.pageInit.postValue(0)
        viewModel.body.put("user_id", prefHelper.getStringFromShared(SharedPrefKeys.USER_ID).toString())
        viewModel.body.put("keyword", keyword)
        viewModel.body.put("sort", sort)
        viewModel.body.put("filter", filter)
        viewModel.token = prefHelper.getStringFromShared(SharedPrefKeys.TOKEN).toString()

        val adapter = InvoiceAdapter({viewModel.retry()}, object : InvoiceAdapter.InvoiceClick{
            override fun viewDetail(invoice: Invoice) {
                val viewDetailDialog = InvoiceDialog.viewDetail(requireActivity(),
                    cancelable = true,
                    labelPositiveButton = getString(R.string.srTxtBtnPositive),
                    labelNegativeButton = getString(R.string.srTxtBtnNegative),
                    positiveAction = {},
                    negativeAction = {},
                    invoice = invoice,
                    language = prefHelper.getStringFromShared(SharedPrefKeys.LANGUAGE).toString()
                )
                viewDetailDialog.show()

            }
        }, prefHelper.getStringFromShared(SharedPrefKeys.LANGUAGE).toString())

        viewModel.pos.observe(requireActivity(), Observer {
            adapter.submitList(it) {
                val layoutManager = binding.rcyInvoice.layoutManager as LinearLayoutManager
                val position = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (position != RecyclerView.NO_POSITION) {
                    binding.rcyInvoice.scrollToPosition(position)
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

        binding.rcyInvoice.adapter = adapter
        binding.rcyInvoice.addItemDecoration(
            DividerItemDecoration(requireContext(),
                DividerItemDecoration.VERTICAL)
        )

        var totalScrollYUp = 0
        binding.rcyInvoice.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

    private fun initToolbar() {
        binding.lytToolbar.lblTitleToolbarHome.text = getString(R.string.invoice)
        binding.lytToolbar.imgBackToolbarHome.visibility = View.GONE
        binding.lytToolbar.imgSearchToolbarHome.visibility = View.VISIBLE
        binding.lytToolbar.imgSearchToolbarHome.setOnClickListener { _ -> toggleSearchLayout()}
        binding.lytToolbar.lytNotificationToolbarHome.visibility = View.VISIBLE
        binding.lytToolbar.lytNotificationToolbarHome.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }
        binding.lytToolbar.imgBackToolbarHome.setOnSafeClickListener {
            requireActivity().onBackPressed()
        }

        binding.edtSearch.setOnEditorActionListener(TextView.OnEditorActionListener() {
            textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                loadInvoice(textView.text.toString(), sort, createRequestFilter())
                true;
            }
            false;
        })

        binding.clearIcon.setOnClickListener(View.OnClickListener { view ->
            edtSearch.setText("")
            toggleSearchLayout()
            loadInvoice(sort = sort, filter = createRequestFilter())
        })
    }

    private fun toggleSearchLayout() {
        if (searchShown == false) {
            binding.searchLayout.alpha = (0.0f)
            binding.searchLayout.visibility = View.VISIBLE

            binding.searchLayout.animate()
                .translationY(0.0f)
                .alpha(1.0f)
                .setListener(null);
            searchShown = true
        } else {
            binding.searchLayout.animate()
                .translationY(0.0f)
                .alpha(0.0f)
                .setListener(object: AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        binding.searchLayout.visibility = View.GONE
                        searchShown = false
                    }
                });
        }
    }

}