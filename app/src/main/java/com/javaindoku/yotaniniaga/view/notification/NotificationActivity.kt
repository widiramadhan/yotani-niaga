package com.javaindoku.yotaniniaga.view.notification

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseActivity
import com.javaindoku.yotaniniaga.databinding.ActivityNotificationBinding
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import com.javaindoku.yotaniniaga.view.notification.farmer.NewsNotificationFragment
import com.javaindoku.yotaniniaga.view.notification.farmer.OrderNotificationFragment
import com.javaindoku.yotaniniaga.view.notification.farmer.PaymentNotificationFragment
import com.javaindoku.yotaniniaga.view.report.adapter.PaymentFragmentAdapter
import com.javaindoku.yotaniniaga.viewmodel.OrderNotificationViewModel
import dagger.android.AndroidInjection
import javax.inject.Inject

class NotificationActivity : BaseActivity() {
    private lateinit var binding: ActivityNotificationBinding

    @Inject
    lateinit var viewModel: OrderNotificationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification)
        initToolbar()
        val adapter = PaymentFragmentAdapter(supportFragmentManager)
        adapter.addFragment(OrderNotificationFragment(), getString(R.string.order))
        adapter.addFragment(PaymentNotificationFragment(), getString(R.string.payment))
        adapter.addFragment(NewsNotificationFragment(), getString(R.string.news))
        binding.viewPagerNotification.offscreenPageLimit = 3
        binding.viewPagerNotification.adapter = adapter
        binding.tabNotification.setupWithViewPager(binding.viewPagerNotification)

    }


    private fun initToolbar() {
        binding.lytToolbar.lblTitleToolbarHome.text = getString(R.string.notification)
        binding.lytToolbar.lytNotificationToolbarHome.visibility = View.VISIBLE
        binding.lytToolbar.imgNewNotificationIndicatorHome.visibility = View.GONE
        binding.lytToolbar.imgNotificationToolbarHome.setColorFilter(ContextCompat.getColor(this, R.color.colorYellow))
        binding.lytToolbar.imgSearchToolbarHome.visibility = View.GONE
        binding.lytToolbar.imgBackToolbarHome.setOnSafeClickListener {
            onBackPressed()
        }
    }

    private fun newNotification(totalNotification: Int) {

    }

    private fun clearNotification() {

    }


}