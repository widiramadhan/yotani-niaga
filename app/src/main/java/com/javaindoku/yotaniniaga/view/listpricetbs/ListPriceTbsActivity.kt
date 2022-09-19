package com.javaindoku.yotaniniaga.view.listpricetbs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseActivity
import com.javaindoku.yotaniniaga.databinding.ActivityListPriceTbsBinding
import com.javaindoku.yotaniniaga.service.network.Status
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import com.javaindoku.yotaniniaga.view.listpricetbs.adapter.ListPriceAdapter
import com.javaindoku.yotaniniaga.viewmodel.ListPriceTbsViewModel
import dagger.android.AndroidInjection
import javax.inject.Inject

class ListPriceTbsActivity : BaseActivity() {
    private lateinit var binding: ActivityListPriceTbsBinding

    @Inject
    lateinit var viewModel: ListPriceTbsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list_price_tbs)
        binding.appBarLayout.lblTitleToolbarHome.text = getString(R.string.listPriceTbs)
        binding.appBarLayout.imgBackToolbarHome.setOnClickListener {
            onBackPressed()
        }

        binding.rcyPriceTbs.layoutManager = LinearLayoutManager(this)
        binding.fabScrollUp.setOnSafeClickListener {
            binding.rcyPriceTbs.smoothScrollToPosition(0)
        }
        loadPriceTbs()
    }

    private fun loadPriceTbs () {
        viewModel.pageInit.postValue(0)

        val adapter = ListPriceAdapter({viewModel.retry()}, this)

        viewModel.pos.observe(this, Observer {
            adapter.submitList(it) {
                val layoutManager = binding.rcyPriceTbs.layoutManager as LinearLayoutManager
                val position = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (position != RecyclerView.NO_POSITION) {
                    binding.rcyPriceTbs.scrollToPosition(position)
                }
            }
        })

        viewModel.networkState.observe(this, Observer {
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

        binding.rcyPriceTbs.adapter = adapter
        binding.rcyPriceTbs.addItemDecoration(
            DividerItemDecoration(this,
            DividerItemDecoration.VERTICAL)
        )

        var totalScrollYUp = 0
        binding.rcyPriceTbs.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                totalScrollYUp += dy

                if (dx == 0 && totalScrollYUp < 200) {
                    if (binding.lytFilter.root.isVisible) {
                        binding.lytFilter.root.visibility = View.GONE
                    }
                } else if (dx >= 0 && totalScrollYUp > 200) {
                    if (!binding.lytFilter.root.isVisible) {
                        binding.lytFilter.root.visibility = View.VISIBLE
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