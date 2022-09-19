package com.javaindoku.yotaniniaga.view.news

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseActivity
import com.javaindoku.yotaniniaga.databinding.ActivityNewsDetailBinding
import com.javaindoku.yotaniniaga.model.news.NewsData
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.utilities.dialog.CustomDialog
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import com.javaindoku.yotaniniaga.view.mainkoperasi.adapter.NewsHomeAdapter
import com.javaindoku.yotaniniaga.view.news.adapter.NewsAdapter
import com.javaindoku.yotaniniaga.view.notification.NotificationActivity
import com.javaindoku.yotaniniaga.viewmodel.NewsDetailViewModel
import dagger.android.AndroidInjection
import java.text.SimpleDateFormat
import javax.inject.Inject

class NewsDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityNewsDetailBinding

    @Inject
    lateinit var viewModel: NewsDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_news_detail)
        val selectedNews: NewsData = intent.getSerializableExtra(NEWS_SELECTED) as NewsData
        initToolbar()
        observeViewModel()
        viewModel.getNewsDetail(selectedNews.newsId)
    }

    private fun observeViewModel() {
        val loadingDialog = loadingDialog(this)
//        val loadingDialog = CustomDialog.showLoadingDialog(this, true)
        viewModel.newsResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(this@NewsDetailActivity, getString(it.messageInt!!), Toast.LENGTH_SHORT)
                    .show()
            } else {
                loadingDialog.dismiss()
                Glide.with(this).load(it.data!!.data[0].newsImage).placeholder(R.color.colorLoading).into(binding.imgNews)
                binding.lblTitleNews.text = it.data!!.data[0].newsTitle
//                val dateString = "2020-08-21 15:48:33 GMT+0700"
                val dateString = it.data!!.data[0].newsDate
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val dateFormated = simpleDateFormat.parse(dateString)
                val simpleDateFormat2 = SimpleDateFormat("EEEE, dd MMMM yyyy | HH:mm Z")
                val dateShow = simpleDateFormat2.format(dateFormated).replace("+0700", "WIB").replace("+0800", "WITA").replace("+0900", "WIT")
                binding.lblDateNews.text = dateShow
                binding.lblNewsDesc.text = it.data!!.data[0].newsDescription
            }
        })
    }

    private fun initToolbar() {
        binding.appBarLayout.lblTitleToolbarHome.text = getString(R.string.news)
        binding.appBarLayout.lytNotificationToolbarHome.visibility = View.VISIBLE
        binding.appBarLayout.lytNotificationToolbarHome.setOnSafeClickListener {
            startActivity(Intent(applicationContext, NotificationActivity::class.java))
        }
        binding.appBarLayout.imgBackToolbarHome.setOnSafeClickListener {
            onBackPressed()
        }
    }

    companion object {
        const val NEWS_SELECTED = "newsSelected"
    }
}