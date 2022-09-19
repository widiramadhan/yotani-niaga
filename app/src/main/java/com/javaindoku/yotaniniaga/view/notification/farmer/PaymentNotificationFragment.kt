package com.javaindoku.yotaniniaga.view.notification.farmer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseFragment
import com.javaindoku.yotaniniaga.databinding.FragmentOrderNotificationBinding
import com.javaindoku.yotaniniaga.di.factory.ViewModelFactory
import com.javaindoku.yotaniniaga.model.notification.NotificationBody
import com.javaindoku.yotaniniaga.model.notification.NotificationData
import com.javaindoku.yotaniniaga.model.notification.NotificationReadBody
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.service.network.Status
import com.javaindoku.yotaniniaga.utilities.SharedPrefKeys
import com.javaindoku.yotaniniaga.view.notification.adapter.NotificationAdapter
import com.javaindoku.yotaniniaga.viewmodel.PaymentNotificationViewModel
import javax.inject.Inject

class PaymentNotificationFragment : BaseFragment() {
    private lateinit var binding: FragmentOrderNotificationBinding

    @Inject
    lateinit var viewModel: PaymentNotificationViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_order_notification,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.run {
            ViewModelProvider(this, viewModelFactory).get(
                PaymentNotificationViewModel::class.java
            )
        } ?: throw Exception("Invalid Activity")
        binding.rcyOrderNotification.layoutManager = LinearLayoutManager(requireContext())
        observeViewModel()
        loadNotification("")
        binding.edtSearchOrderNotification.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                loadNotification(binding.edtSearchOrderNotification.text.toString())
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun loadNotification(search: String) {
        requireActivity().let {
            viewModel.notifBody = NotificationBody(
                user_id = prefHelper.getStringFromShared(SharedPrefKeys.USER_ID)!!,
                type = "P",
                search = search,
                page = "1",
                limit = "10"
            )

            viewModel.token = prefHelper.getStringFromShared(SharedPrefKeys.TOKEN)!!
            viewModel.pageInit.postValue(0)

            val adapter = NotificationAdapter({ viewModel.retry() }, requireActivity(), object : NotificationAdapter.NotifClick {
                override fun readNotif(notificationData: NotificationData) {
                    val userId = prefHelper.getStringFromShared(SharedPrefKeys.USER_ID)!!
                    val token = prefHelper.getStringFromShared(SharedPrefKeys.TOKEN)!!
                    val body = NotificationReadBody(
                        userId,
                        notificationData.id.toString()
                    )
                    viewModel.readNotification(body, token)
                }
            })

            viewModel.pos.observe(viewLifecycleOwner, Observer {
                adapter.submitList(it) {
                    val layoutManager =
                        binding.rcyOrderNotification.layoutManager as LinearLayoutManager
                    val position = layoutManager.findFirstCompletelyVisibleItemPosition()
                    if (position != RecyclerView.NO_POSITION) {
                        binding.rcyOrderNotification.scrollToPosition(position)
                    }
                }
            })

            viewModel.networkState.observe(viewLifecycleOwner, Observer {
                val networkState = it
                adapter.setNetworkState(it)
                if (it.status == Status.FAILED) {

                } else if (it.status == Status.FAILED) {

                } else if (it.status == Status.RUNNING) {

                } else if (it.status == Status.SUCCESS) {

                }
            })

            binding.rcyOrderNotification.adapter = adapter
            /*binding.rcyOrderNotification.addItemDecoration(
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            )*/

            var totalScrollYUp = viewModel.totalScrollYRecyclerView

            binding.rcyOrderNotification.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    totalScrollYUp += dy
                }
            })
        }
    }

    private fun observeViewModel() {
        viewModel.readNotificationResult.observe(requireActivity(), {
            if (it.status === ApiResult.Status.LOADING) {
                //loadingDialog.show()
            } else if (it.status === ApiResult.Status.ERROR) {
                //Toast.makeText(requireContext(), getString(it.messageInt!!), Toast.LENGTH_SHORT).show()
                //loadingDialog.dismiss()
            } else {
                //loadingDialog.dismiss()
                if (it.data?.isSuccess == false) {
                    //Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.refresh()
                }
            }
        })
    }
}