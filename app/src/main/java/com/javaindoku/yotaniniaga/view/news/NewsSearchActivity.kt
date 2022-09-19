package com.javaindoku.yotaniniaga.view.news

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseActivity
import com.javaindoku.yotaniniaga.databinding.ActivityNewsSearchBinding
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener

class NewsSearchActivity : BaseActivity() {
    private lateinit var binding: ActivityNewsSearchBinding

    companion object {
        const val SEARCH_NEWS_RC = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_news_search)
        binding.btnBackSearchFragment.setOnSafeClickListener {
            finish()
        }
        binding.btnOkSearchNews.setOnSafeClickListener {
            val intentResult = Intent()
            intentResult.putExtra(NewsActivity.SEARCH_NEWS, binding.edtSearchNews.text.toString())
            setResult(Activity.RESULT_OK, intentResult)
            finish()
        }
    }
}