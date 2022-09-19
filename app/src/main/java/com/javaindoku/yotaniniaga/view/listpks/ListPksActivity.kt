package com.javaindoku.yotaniniaga.view.listpks

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseActivity
import com.javaindoku.yotaniniaga.databinding.ActivityListPksBinding
import com.javaindoku.yotaniniaga.model.news.NewsData
import com.javaindoku.yotaniniaga.model.pks.PKS
import com.javaindoku.yotaniniaga.view.listpks.adapter.PksAdapter
import com.javaindoku.yotaniniaga.view.news.NewsDetailActivity
import com.javaindoku.yotaniniaga.view.news.adapter.NewsAdapter
import dagger.android.AndroidInjection

class ListPksActivity : BaseActivity() {
    private lateinit var binding: ActivityListPksBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list_pks)
        binding.rcyListPks.layoutManager = LinearLayoutManager(this)
        val listPks = listOf(
            PKS("PT. Padang Palma", "1 Ton", "1000", "2013", "1 Hektar", "Bukit asam haliman"),
            PKS("PT. Padang Palma", "1 Ton", "1000", "2013", "1 Hektar", "Bukit asam haliman")
        )
        val adapter = PksAdapter(listPks, object : PksAdapter.OnClickPks {
            override fun toEditPks(pks: PKS) {

            }
        })
        binding.rcyListPks.adapter = adapter
        binding.appBarLayout.lblTitleToolbarHome.text = getString(R.string.list_pks_title)
        binding.appBarLayout.imgBackToolbarHome.setOnClickListener { finish() }
    }

}