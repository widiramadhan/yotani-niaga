package com.javaindoku.yotaniniaga.view.mainkoperasi.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.shape.CornerFamily
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseFragment
import com.javaindoku.yotaniniaga.databinding.FragmentHomeKoperasiBinding
import com.javaindoku.yotaniniaga.model.gardenPetani.GardenBody
import com.javaindoku.yotaniniaga.model.gardenPetani.GardenDataSchema
import com.javaindoku.yotaniniaga.model.gardenPetani.GardenKoperasiBody
import com.javaindoku.yotaniniaga.model.news.NewsData
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.utilities.SharedPrefKeys
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import com.javaindoku.yotaniniaga.view.listgarden.ListGardenActivity
import com.javaindoku.yotaniniaga.view.listgarden.adapter.GardenAdapter
import com.javaindoku.yotaniniaga.view.listpks.ListPksActivity
import com.javaindoku.yotaniniaga.view.mainkoperasi.MainKoperasiActivity
import com.javaindoku.yotaniniaga.view.mainkoperasi.adapter.NewsHomeAdapter
import com.javaindoku.yotaniniaga.view.news.NewsActivity
import com.javaindoku.yotaniniaga.view.news.NewsDetailActivity
import com.javaindoku.yotaniniaga.view.news.adapter.NewsAdapter
import com.javaindoku.yotaniniaga.view.notification.NotificationActivity
import com.javaindoku.yotaniniaga.view.report.ReportActivity
import com.javaindoku.yotaniniaga.viewmodel.HomeKoperasiViewModel
import java.util.*
import javax.inject.Inject


class HomeKoperasiFragment : BaseFragment() {
    private lateinit var binding: FragmentHomeKoperasiBinding

    @Inject
    lateinit var viewModel: HomeKoperasiViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home_koperasi, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val radius = resources.getDimension(R.dimen._30sdp)
        binding.shpHeaderHome.shapeAppearanceModel = binding.shpHeaderHome.shapeAppearanceModel.toBuilder().setBottomLeftCorner(CornerFamily.ROUNDED, radius)
            .setBottomRightCorner(CornerFamily.ROUNDED, radius)
            .build()

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 45)

        binding.rcyNewsHome.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.rcyGardenHome.layoutManager = LinearLayoutManager(requireContext())

        binding.lblSeeAllNews.setOnSafeClickListener {
            startActivity(Intent(activity, NewsActivity::class.java))
        }
        binding.lytListPKS.setOnSafeClickListener {
            startActivity(Intent(activity, ListPksActivity::class.java))
        }
        binding.lytListKebun.setOnSafeClickListener {
            startActivity(Intent(activity, ListGardenActivity::class.java))
        }
        binding.lytReport.setOnSafeClickListener {
            startActivity(Intent(activity, ReportActivity::class.java))
        }

        binding.lytManageUsers.setOnSafeClickListener {
            (activity as MainKoperasiActivity).updateLocale()
        }
        binding.imgNotificationToolbarHome.setOnClickListener {
            startActivity(Intent(activity, NotificationActivity::class.java))
        }
        observeViewModel()
        getGarden()
        viewModel.getNews()
    }

    private fun getGarden() {
        if (prefHelper.getStringFromShared(SharedPrefKeys.KOPERASI_ID).isNullOrEmpty()) {
            viewModel.gardenBody = GardenBody(
                user_id = prefHelper.getStringFromShared(SharedPrefKeys.USER_ID)!!,
                petaniId = prefHelper.getStringFromShared(SharedPrefKeys.PETANI_ID)!!,
                orderBy = "id",
                sort= "asc"
            )
            viewModel.getGardenPetani(prefHelper.getStringFromShared(SharedPrefKeys.TOKEN)!!)
        } else {
            viewModel.gardenKoperasiBody = GardenKoperasiBody(
                user_id = prefHelper.getStringFromShared(SharedPrefKeys.USER_ID)!!,
                koperasi_id = prefHelper.getStringFromShared(SharedPrefKeys.KOPERASI_ID)!!
            )
            viewModel.getGardenKoperasi(prefHelper.getStringFromShared(SharedPrefKeys.TOKEN)!!)
        }
    }

    private fun observeViewModel()  {
        val loadingDialog = loadingDialog(requireActivity())
        viewModel.newsResult.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(requireContext(), getString(it.messageInt!!), Toast.LENGTH_SHORT)
                    .show()
            } else {
                loadingDialog.dismiss()
                val adapter = NewsHomeAdapter(it.data!!.data, object : NewsAdapter.OnClickNews{
                    override fun toDetailNews(newsData: NewsData) {
                        startActivity(Intent(requireContext(), NewsDetailActivity::class.java).putExtra(
                            NewsDetailActivity.NEWS_SELECTED, newsData))
                    }
                })
                binding.rcyNewsHome.adapter = adapter
            }
        })

        viewModel.gardenResult.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(requireContext(), getString(it.messageInt!!), Toast.LENGTH_SHORT)
                    .show()
            } else {
                loadingDialog.dismiss()
                val adapterGarden = GardenAdapter(requireContext(), it.data!!.data, object : GardenAdapter.OnClickGarden {
                    override fun toEditGarden(pks: GardenDataSchema) {

                    }
                }, childFragmentManager)
                binding.rcyGardenHome.adapter = adapterGarden
            }
        })
    }

    override fun onResume() {
        super.onResume()
        getGarden()
    }
}