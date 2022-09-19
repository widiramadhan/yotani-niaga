package com.javaindoku.yotaniniaga.view.news

import android.content.Intent
import android.os.Bundle
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
import com.javaindoku.yotaniniaga.databinding.ActivityNewsBinding
import com.javaindoku.yotaniniaga.model.deliverykoperasi.DeliveryKoperasi
import com.javaindoku.yotaniniaga.model.news.NewsData
import com.javaindoku.yotaniniaga.service.network.Status
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import com.javaindoku.yotaniniaga.view.delivery.adapter.DeliveryKoperasiAdapter
import com.javaindoku.yotaniniaga.view.delivery.dialog.DeliveryDialog
import com.javaindoku.yotaniniaga.view.news.NewsDetailActivity.Companion.NEWS_SELECTED
import com.javaindoku.yotaniniaga.view.news.adapter.NewsAdapter
import com.javaindoku.yotaniniaga.viewmodel.NewsViewModel
import com.javaindoku.yotaniniaga.viewmodel.OrderNotificationViewModel
import dagger.android.AndroidInjection
import javax.inject.Inject

class NewsActivity : BaseActivity() {
    private lateinit var binding : ActivityNewsBinding

    @Inject
    lateinit var viewModel: NewsViewModel

    companion object {
        const val SEARCH_NEWS = "searchNews"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_news)
        binding.rcyNews.layoutManager = LinearLayoutManager(this)
        binding.fabScrollUp.setOnSafeClickListener {
            binding.rcyNews.smoothScrollToPosition(0)
        }

        initToolbar()
        loadNews()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == NewsSearchActivity.SEARCH_NEWS_RC && resultCode == RESULT_OK) {
            val keywordSearch = data?.getStringExtra(SEARCH_NEWS)
            Toast.makeText(applicationContext, keywordSearch, Toast.LENGTH_SHORT).show()
        }
    }

    private fun initToolbar() {
        binding.appBarLayout.lblTitleToolbarHome.text = getString(R.string.news)
        binding.appBarLayout.lytNotificationToolbarHome.visibility = View.VISIBLE
        binding.appBarLayout.imgSearchToolbarHome.visibility = View.VISIBLE
        binding.appBarLayout.imgSearchToolbarHome.setOnSafeClickListener {
            val intent = Intent(applicationContext, NewsSearchActivity::class.java)
            startActivityForResult(intent, NewsSearchActivity.SEARCH_NEWS_RC)
        }
        binding.appBarLayout.imgBackToolbarHome.setOnSafeClickListener {
            onBackPressed()
        }
    }

    private fun loadNews () {
        viewModel.pageInit.postValue(0)

        val adapter = NewsAdapter({viewModel.retry()}, object : NewsAdapter.OnClickNews {
            override fun toDetailNews(newsData: NewsData) {
                startActivity(Intent(applicationContext, NewsDetailActivity::class.java).putExtra(NEWS_SELECTED, newsData))
            }
        })

        viewModel.pos.observe(this, Observer {
            adapter.submitList(it) {
                val layoutManager = binding.rcyNews.layoutManager as LinearLayoutManager
                val position = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (position != RecyclerView.NO_POSITION) {
                    binding.rcyNews.scrollToPosition(position)
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

        binding.rcyNews.adapter = adapter

        var totalScrollYUp = viewModel.totalScrollYRecyclerView
        binding.rcyNews.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                totalScrollYUp += dy
                viewModel.totalScrollYRecyclerView = totalScrollYUp

//                if (dx == 0 && totalScrollYUp < 200) {
//                    if (binding.lytSorting.root.isVisible) {
//                        binding.lytSorting.root.visibility = View.GONE
//                    }
//                } else if (dx >= 0 && totalScrollYUp > 200) {
//                    if (!binding.lytSorting.root.isVisible) {
//                        binding.lytSorting.root.visibility = View.VISIBLE
//                    }
//                }

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