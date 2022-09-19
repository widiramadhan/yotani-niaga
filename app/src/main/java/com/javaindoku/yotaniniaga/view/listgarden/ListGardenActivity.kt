package com.javaindoku.yotaniniaga.view.listgarden

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseActivity
import com.javaindoku.yotaniniaga.databinding.ActivityListGardenBinding

class ListGardenActivity : BaseActivity() {
    private lateinit var binding: ActivityListGardenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list_garden)
//        initToolbar()
        binding.rcyListGarden.layoutManager = LinearLayoutManager(this)
//        val listGarden = listOf(
//            Garden("Kebun 1", "1 Ton", "1000", "2013", "1 Hektar", "Bukit asam haliman", numberOfPalmSupplied = "1000"),
//            Garden("Kebun 1", "1 Ton", "1000", "2013", "1 Hektar", "Bukit asam haliman", numberOfPalmSupplied = "1000")
//        )
//        val adapter = GardenAdapter(listGarden, object : GardenAdapter.OnClickGarden {
//            override fun toEditGarden(pks: Garden) {
//
//            }
//        })
//        binding.rcyListGarden.adapter = adapter
        binding.appBarLayout.lblTitleToolbarHome.text = getString(R.string.list_garden_title)
        binding.appBarLayout.imgBackToolbarHome.setOnClickListener { finish() }
    }

//    private fun initToolbar() {
//        binding.toolbar.setNavigationIcon(R.drawable.ic_back_white)
//        setSupportActionBar(binding.toolbar)
//        supportActionBar!!.title = getString(R.string.list_kebun)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_news, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            android.R.id.home -> finish()
//            R.id.actSearch -> {
//
//            }
//        }
//        return true
//    }
}