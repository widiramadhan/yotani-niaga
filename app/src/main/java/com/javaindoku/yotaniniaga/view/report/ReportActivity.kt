package com.javaindoku.yotaniniaga.view.report

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseActivity
import com.javaindoku.yotaniniaga.databinding.ActivityReportBinding
import com.javaindoku.yotaniniaga.view.report.adapter.PaymentFragmentAdapter
import com.javaindoku.yotaniniaga.view.report.fragment.PaymentFragment
import dagger.android.AndroidInjection

class ReportActivity : BaseActivity() {
    private lateinit var binding: ActivityReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report)
        initToolbar()
        val adapter = PaymentFragmentAdapter(supportFragmentManager)
        adapter.addFragment(PaymentFragment(), getString(R.string.payment))
        adapter.addFragment(PaymentFragment(), getString(R.string.transaction))
        binding.viewPagerReport.offscreenPageLimit = 1
        binding.viewPagerReport.adapter = adapter
        binding.tabReport.setupWithViewPager(binding.viewPagerReport)
    }

    private fun initToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_back_white)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = getString(R.string.report)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_news, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.actSearch -> {

            }
        }
        return true
    }
}