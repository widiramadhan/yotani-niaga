package com.javaindoku.yotaniniaga.view.profilePetani

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseFragment
import com.javaindoku.yotaniniaga.databinding.FragmentProfilePetaniBinding
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
import com.javaindoku.yotaniniaga.view.detailGarden.GardenViewDetailActivity
import com.javaindoku.yotaniniaga.view.login.LoginActivity
import com.javaindoku.yotaniniaga.view.mainkoperasi.FragmentInterface
import com.javaindoku.yotaniniaga.view.profilePetani.adapter.GardenProfileAdapter
import com.javaindoku.yotaniniaga.view.profilePetani.adapter.ProfilePetaniDetailActivity
import com.javaindoku.yotaniniaga.viewmodel.ProfilePetaniViewModel
import javax.inject.Inject


class ProfilePetaniFragment : BaseFragment(), FragmentInterface {
    private lateinit var binding : FragmentProfilePetaniBinding
    private lateinit var loadingDialog : Dialog
    @Inject
    lateinit var viewModel: ProfilePetaniViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_profile_petani, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = loadingDialog(requireActivity())
        binding.rcyFarmer.layoutManager = LinearLayoutManager(requireContext())
        loadGarden()
        initToolbar()
        binding.lblNameUser.setText(prefHelper.getStringFromShared(SharedPrefKeys.NAME))
        CustomProfile.showRemoteImageUsingGlide(
            requireContext(),
            binding.cimgProfilePicture,
            prefHelper.getStringFromShared(SharedPrefKeys.IMAGE_PROFILE),
            R.drawable.ic_businessman
        )
        binding.lytViewProfile.setOnSafeClickListener {
            startActivity(Intent(requireContext(), ProfilePetaniDetailActivity::class.java))
        }
        binding.btnAddGarden.setOnSafeClickListener {
            startActivity(Intent(requireContext(), AddGardenActivity::class.java))
        }
    }

    private fun initToolbar() {
        binding.lytToolbar.lblTitleToolbarHome.text = getString(R.string.profile)
        binding.lytToolbar.imgBackToolbarHome.visibility = View.GONE
        binding.lytToolbar.imgSearchToolbarHome.visibility = View.GONE
        binding.lytToolbar.imgLogout.visibility = View.VISIBLE
        binding.lytToolbar.imgLogout.setOnSafeClickListener {
            logout()
        }
        binding.lytToolbar.imgBackToolbarHome.setOnSafeClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun logout() {
        CustomDialog.customDialogTwoButton(activity = requireActivity(),
        cancelable = true,
        title = getString(R.string.srSure),
        message = getString(R.string.srToLogout),
        labelPositiveButton = getString(R.string.srYes),
        labelNegativeButton = getString(R.string.no),
        positiveAction = {
            prefHelper.clearDataLogout()
            val intentLogin = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intentLogin)
            requireActivity().finish()
        },
        negativeAction = {}).show()
    }

    private fun loadGarden () {
        requireActivity().let {
            viewModel.gardenBody = GardenBody(
                user_id = prefHelper.getStringFromShared(SharedPrefKeys.USER_ID)!!,
                petaniId = prefHelper.getStringFromShared(SharedPrefKeys.PETANI_ID)!!,
                orderBy = "id",
                sort= "asc"
            )
            if(viewModel.pageInit.value == null)
            {
                viewModel.token = prefHelper.getStringFromShared(SharedPrefKeys.TOKEN)!!
                viewModel.pageInit.postValue(0)
            }

            val adapter = GardenProfileAdapter({viewModel.retry()}, requireActivity(), object : GardenProfileAdapter.ProfileGardenClick{
                override fun viewDetail(gardenData: GardenDataSchema) {
                    val intent = Intent(requireContext(), GardenViewDetailActivity::class.java)
                    intent.putExtra(ConstValue.GARDEN_ID, gardenData.id)
                    intent.putExtra(ConstValue.EDIT_MODE, "false")
                    startActivity(intent)
                }

                override fun edit(gardenData: GardenDataSchema) {
                    val intent = Intent(requireContext(), AddGardenActivity::class.java)
                    intent.putExtra(ConstValue.GARDEN_ID, gardenData.id)
                    intent.putExtra(ConstValue.EDIT_MODE, "true")
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

            observeViewModel(adapter)

            binding.rcyFarmer.adapter = adapter
            binding.rcyFarmer.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
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
    }

    private fun observeViewModel(adapter: GardenProfileAdapter) {
        viewModel.pos.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it) {
                val layoutManager = binding.rcyFarmer.layoutManager as LinearLayoutManager
                val position = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (position != RecyclerView.NO_POSITION) {
                    binding.rcyFarmer.scrollToPosition(position)
                }
            }
        })

        viewModel.networkState.observe(viewLifecycleOwner, Observer {
            adapter.setNetworkState(it)
            if (it.status == Status.FAILED) {

            } else if (it.status == Status.FAILED) {

            } else if (it.status == Status.RUNNING) {

            } else if (it.status == Status.SUCCESS) {

            }
        })

        viewModel.actionResult.observe(requireActivity(), Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                CustomDialog.dialogOneButton(
                    requireActivity(),
                    true,
                    getString(R.string.success),
                    getString(R.string.profile_updated),
                    R.drawable.ic_dialog_success,
                    true,
                    getString(R.string.srOk),
                    {activity -> viewModel.refresh() },
                ).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    override fun fragmentOnResume() {
        viewModel.refresh()
    }
}