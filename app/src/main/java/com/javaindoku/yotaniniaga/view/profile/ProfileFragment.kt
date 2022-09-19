package com.javaindoku.yotaniniaga.view.profile

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseFragment
import com.javaindoku.yotaniniaga.databinding.*
import com.javaindoku.yotaniniaga.model.profile.Farmer
import com.javaindoku.yotaniniaga.model.profilePetaniDetail.Data
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.service.network.Status
import com.javaindoku.yotaniniaga.utilities.CustomProfile
import com.javaindoku.yotaniniaga.utilities.PrefHelper
import com.javaindoku.yotaniniaga.utilities.SharedPrefKeys
import com.javaindoku.yotaniniaga.utilities.dialog.CustomDialog
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import com.javaindoku.yotaniniaga.view.addFarmer.AddFarmerActivity
import com.javaindoku.yotaniniaga.view.gardenmanagement.GardenManagementActivity
import com.javaindoku.yotaniniaga.view.login.LoginActivity
import com.javaindoku.yotaniniaga.view.profile.adapter.FarmerAdapter
import com.javaindoku.yotaniniaga.view.profile.adapter.ProfileDetailActivity
import com.javaindoku.yotaniniaga.viewmodel.ProfileKoperasiViewModel
import javax.inject.Inject

class ProfileFragment : BaseFragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var loadingDialog: Dialog
    @Inject
    lateinit var viewModel: ProfileKoperasiViewModel
    var mobileNumber = ""

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
            inflater, R.layout.fragment_profile, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rcyFarmer.layoutManager = LinearLayoutManager(requireContext())
        loadingDialog = loadingDialog(requireActivity())
        loadFarmer()
        initToolbar()
        binding.lblNameUser.setText(prefHelper.getStringFromShared(SharedPrefKeys.NAME))
        CustomProfile.showRemoteImageUsingGlide(
            requireContext(),
            binding.cimgProfilePicture,
            prefHelper.getStringFromShared(SharedPrefKeys.IMAGE_PROFILE),
            R.drawable.ic_businessman
        )
        binding.lytViewProfile.setOnSafeClickListener {
            startActivity(Intent(requireContext(), ProfileDetailActivity::class.java))
        }
        binding.btnAddFarmer.setOnSafeClickListener {
            val enterMobilePhoneDialog = Dialog(requireContext())
            val dialogBinding = DialogEnterMobileBinding.inflate(
                LayoutInflater.from(requireContext()),
                binding.root as ViewGroup,
                false
            )
            dialogBinding.imgClose.setOnClickListener { enterMobilePhoneDialog.dismiss() }
            dialogBinding.addFarmerButton.setOnClickListener{
                mobileNumber = dialogBinding.mobilePhoneInput.text.toString()
                viewModel.checkIsFarmerBound(
                    prefHelper.getStringFromShared(SharedPrefKeys.USER_ID).toString(),
                    dialogBinding.mobilePhoneInput.text.toString(),
                    prefHelper.getStringFromShared(SharedPrefKeys.TOKEN).toString()
                )
                enterMobilePhoneDialog.dismiss()
            }
            enterMobilePhoneDialog.setContentView(dialogBinding.root)
            enterMobilePhoneDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            enterMobilePhoneDialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            enterMobilePhoneDialog.setCancelable(true)
            enterMobilePhoneDialog.show()
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.checkPetaniResult.observe(requireActivity(), Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
            } else {
                loadingDialog.dismiss()
                val result = it.data
                val intent = Intent(requireContext(), AddFarmerActivity::class.java)
                if (!result?.isSuccess!!) {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                } else {
                    if (result?.data != null) {
                        intent.putExtra("farmerId", result.data!!.petaniId)
                        intent.putExtra("farmerUserId", result.data!!.userIdPetani)
                    }
                    intent.putExtra("mobile", mobileNumber)
                    startActivity(intent)
                }
            }
        })

        viewModel.postResult.observe(requireActivity(), Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
            } else {
                loadingDialog.dismiss()
                CustomDialog.dialogOneButton(
                    requireActivity(),
                    true,
                    getString(R.string.success),
                    getString(R.string.farmer_added),
                    R.drawable.ic_dialog_success,
                    true,
                    getString(R.string.srOk),
                    {loadFarmer()},
                ).show()
            }
        })
    }

    private fun initToolbar() {
        binding.lytToolbar.lblTitleToolbarHome.text = getString(R.string.profile)
        binding.lytToolbar.imgBackToolbarHome.visibility = View.GONE
        binding.lytToolbar.imgSearchToolbarHome.visibility = View.GONE
        binding.lytToolbar.imgLogout.visibility = View.VISIBLE
        binding.lytToolbar.imgLogout.setOnSafeClickListener { logout() }
        binding.lytToolbar.imgBackToolbarHome.setOnSafeClickListener { requireActivity().onBackPressed() }
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

    private fun loadFarmer() {
        viewModel.pageInit.postValue(0)
        viewModel.jsonBody.put("user_id", prefHelper.getStringFromShared(SharedPrefKeys.USER_ID))
        viewModel.jsonBody.put(
            "koperasi_id",
            prefHelper.getStringFromShared(SharedPrefKeys.KOPERASI_ID)
        )
        viewModel.token = prefHelper.getStringFromShared(SharedPrefKeys.TOKEN).toString()
        val adapter = FarmerAdapter({ viewModel.retry() },
            requireActivity(),
            object : FarmerAdapter.ProfileFarmerClick {
                override fun edit(farmer: Data) {
                    val intent = Intent(requireContext(), AddFarmerActivity::class.java)
                    intent.putExtra("farmerId", farmer.petaniId)
                    intent.putExtra("editMode", true)
                    startActivity(intent)
                }

                override fun delete(farmer: Data) {
                    val confirmationDialog = Dialog(requireContext())
                    val confirmationDialogBinding = DialogConfirmationBinding.inflate(
                        LayoutInflater.from(requireContext()),
                        binding.root as ViewGroup,
                        false
                    )
                    confirmationDialogBinding.lblMessage.text = "Hapus petani ${farmer.namaPetani}?"
                    confirmationDialogBinding.lblTitle.text = getString(R.string.srConfirmation)
                    confirmationDialogBinding.imgStatus.setImageResource(R.drawable.ic_wondering)
                    confirmationDialogBinding.btnConfirmationPositive.text = getString(R.string.yes)
                    confirmationDialogBinding.btnConfirmationNegative.text = getString(R.string.no)
                    confirmationDialogBinding.btnConfirmationNegative.setOnClickListener { confirmationDialog.dismiss() }
                    confirmationDialogBinding.btnConfirmationPositive.setOnClickListener {
                        confirmationDialog.dismiss()
                        viewModel.removeFarmer(
                            prefHelper.getStringFromShared(SharedPrefKeys.USER_ID).toString(),
                            prefHelper.getStringFromShared(SharedPrefKeys.KOPERASI_ID).toString(),
                            farmer.petaniId.toString(),
                            prefHelper.getStringFromShared(SharedPrefKeys.TOKEN).toString(),
                        )
                        confirmationDialog.dismiss()
                    }
                    confirmationDialog.setContentView(confirmationDialogBinding.root)
                    confirmationDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    confirmationDialog.window?.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    confirmationDialog.setCancelable(true)
                    confirmationDialog.show()
                }

                override fun onClick(farmer: Data) {
                    val intent = Intent(requireContext(), GardenManagementActivity::class.java)
                    intent.putExtra("farmerName", farmer.namaPetani)
                    intent.putExtra("farmerId", farmer.petaniId)
                    intent.putExtra("farmerPicture", farmer.foto)
                    startActivity(intent)
                }
            })

        viewModel.pos.observe(requireActivity(), Observer {
            adapter.submitList(it) {
                val layoutManager = binding.rcyFarmer.layoutManager as LinearLayoutManager
                val position = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (position != RecyclerView.NO_POSITION) {
                    binding.rcyFarmer.scrollToPosition(position)
                }
            }
        })

        viewModel.networkState.observe(requireActivity(), Observer {
            val networkState = it
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

        binding.rcyFarmer.adapter = adapter
        binding.rcyFarmer.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        var totalScrollYUp = 0
        binding.rcyFarmer.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalScrollYUp += dy
            }
        })
    }

    override fun onResume() {
        super.onResume()
        loadFarmer()
    }
}