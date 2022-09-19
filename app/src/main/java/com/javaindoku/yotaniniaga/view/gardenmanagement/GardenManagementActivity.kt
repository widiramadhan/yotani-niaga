package com.javaindoku.yotaniniaga.view.gardenmanagement

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseActivity
import com.javaindoku.yotaniniaga.databinding.ActivityGardenManagementBinding
import com.javaindoku.yotaniniaga.model.gardenPetani.GardenBody
import com.javaindoku.yotaniniaga.model.gardenPetani.GardenDataSchema
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.service.network.Status
import com.javaindoku.yotaniniaga.utilities.ConstValue
import com.javaindoku.yotaniniaga.utilities.CustomProfile
import com.javaindoku.yotaniniaga.utilities.SharedPrefKeys
import com.javaindoku.yotaniniaga.utilities.dialog.CustomDialog
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import com.javaindoku.yotaniniaga.view.addGarden.AddGardenActivity
import com.javaindoku.yotaniniaga.view.profilePetani.adapter.GardenProfileAdapter
import com.javaindoku.yotaniniaga.view.profilePetani.adapter.ProfilePetaniDetailActivity
import com.javaindoku.yotaniniaga.viewmodel.GardenManagementViewModel
import dagger.android.AndroidInjection
import javax.inject.Inject

class GardenManagementActivity : BaseActivity() {
    private lateinit var adapter: GardenProfileAdapter
    lateinit var binding: ActivityGardenManagementBinding
    @Inject
    lateinit var viewModel: GardenManagementViewModel
    var farmerId = ""
    private lateinit var loadingDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_garden_management)
        binding.rcyFarmer.layoutManager = LinearLayoutManager(this)
        loadingDialog = loadingDialog(this)
        initToolbar()
        binding.lblNameUser.text = intent.getStringExtra("farmerName")
        farmerId = intent.getStringExtra("farmerId")
        CustomProfile.showRemoteImageUsingGlide(
            this,
            binding.cimgProfilePicture,
            intent.getStringExtra("farmerPicture"),
            R.drawable.ic_businessman
        )
        loadGarden()
        binding.lytViewProfile.setOnSafeClickListener {
            startActivity(Intent(this, ProfilePetaniDetailActivity::class.java))
        }
        binding.btnAddGarden.setOnSafeClickListener {
            val intent = Intent(this, AddGardenActivity::class.java)
            intent.putExtra(ConstValue.FARMER_ID, farmerId)
            startActivity(intent)
        }
        observeViewModel()
    }

    private fun loadGarden() {
        viewModel.token = prefHelper.getStringFromShared(SharedPrefKeys.TOKEN)!!
        viewModel.pageInit.postValue(0)
        viewModel.gardenBody = GardenBody(
            user_id = prefHelper.getStringFromShared(SharedPrefKeys.USER_ID)!!,
            petaniId = farmerId,
            orderBy = "id",
            sort= "asc"
        )

        adapter = GardenProfileAdapter({viewModel.retry()}, this, object : GardenProfileAdapter.ProfileGardenClick{
            override fun viewDetail(gardenData: GardenDataSchema) {
                val intent = Intent(this@GardenManagementActivity, AddGardenActivity::class.java)
                intent.putExtra(ConstValue.GARDEN_ID, gardenData.id)
                intent.putExtra(ConstValue.EDIT_MODE, true)
                intent.putExtra(ConstValue.FARMER_ID, farmerId)
                startActivity(intent)
            }

            override fun edit(gardenData: GardenDataSchema) {
                val intent = Intent(this@GardenManagementActivity, AddGardenActivity::class.java)
                intent.putExtra(ConstValue.GARDEN_ID, gardenData.id)
                intent.putExtra(ConstValue.EDIT_MODE, true)
                intent.putExtra(ConstValue.FARMER_ID, farmerId)
                startActivity(intent)
            }

            override fun delete(gardenData: GardenDataSchema) {
                viewModel.deleteGardenById(
                    prefHelper.getStringFromShared(SharedPrefKeys.TOKEN).toString(),
                    prefHelper.getStringFromShared(SharedPrefKeys.USER_ID).toString(),
                    gardenData.id.toString()
                )
            }
        })

        binding.rcyFarmer.adapter = adapter
        binding.rcyFarmer.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        var totalScrollYUp = viewModel.totalScrollYRecyclerView

        binding.rcyFarmer.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                totalScrollYUp += dy
            }
        })
    }

    private fun observeViewModel() {
        viewModel.pos.observe(this, Observer {
            adapter.submitList(it) {
                val layoutManager = binding.rcyFarmer.layoutManager as LinearLayoutManager
                val position = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (position != RecyclerView.NO_POSITION) {
                    binding.rcyFarmer.scrollToPosition(position)
                }
            }
        })

        viewModel.networkState.observe(this, Observer {
            adapter.setNetworkState(it)
            when (it.status) {
                Status.FAILED -> {

                }
                Status.RUNNING -> {

                }
                Status.SUCCESS -> {

                }
            }
        })

        viewModel.actionResult.observe(this, Observer {
            when (it.status) {
                ApiResult.Status.LOADING -> {
                    loadingDialog.show()
                }
                ApiResult.Status.ERROR -> {
                    loadingDialog.dismiss()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    loadingDialog.dismiss()
                    CustomDialog.dialogOneButton(
                        this,
                        true,
                        getString(R.string.success),
                        getString(R.string.profile_updated),
                        R.drawable.ic_dialog_success,
                        true,
                        getString(R.string.srOk),
                        {activity -> viewModel.refresh() },
                    ).show()
                }
            }
        })
    }

    private fun initToolbar() {
        binding.lytToolbar.lblTitleToolbarHome.text = getString(R.string.farmer_profile)
        binding.lytToolbar.imgBackToolbarHome.visibility = View.VISIBLE
        binding.lytToolbar.imgSearchToolbarHome.visibility = View.GONE
        binding.lytViewProfile.visibility = View.GONE
        binding.lytToolbar.imgBackToolbarHome.setOnSafeClickListener {
            onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        loadGarden()
    }
}