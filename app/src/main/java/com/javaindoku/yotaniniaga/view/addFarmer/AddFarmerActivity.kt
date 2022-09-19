package com.javaindoku.yotaniniaga.view.addFarmer

import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.iceteck.silicompressorr.SiliCompressor
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseActivity
import com.javaindoku.yotaniniaga.databinding.ActivityAddFarmerBinding
import com.javaindoku.yotaniniaga.di.factory.ViewModelFactory
import com.javaindoku.yotaniniaga.model.profilePetaniDetail.Data
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.utilities.ConstValue.FARMER_ID
import com.javaindoku.yotaniniaga.utilities.SharedPrefKeys
import com.javaindoku.yotaniniaga.utilities.dialog.CustomDialog
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import com.javaindoku.yotaniniaga.view.addFarmer.fragment.FarmerAddressFormFragment
import com.javaindoku.yotaniniaga.view.addFarmer.fragment.FarmerDataFormFragment
import com.javaindoku.yotaniniaga.view.addFarmer.listener.AddressChangedListener
import com.javaindoku.yotaniniaga.view.addFarmer.listener.DataChangedListener
import com.javaindoku.yotaniniaga.view.addFarmer.listener.DataErrorListener
import com.javaindoku.yotaniniaga.view.report.adapter.PaymentFragmentAdapter
import com.javaindoku.yotaniniaga.viewmodel.AddFarmerViewModel
import dagger.android.AndroidInjection
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class AddFarmerActivity : BaseActivity(), View.OnClickListener {
    var imageUri = ""
    private var farmer = Data()
    private lateinit var binding : ActivityAddFarmerBinding
    @Inject
    lateinit var viewModel: AddFarmerViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var loadingDialog: Dialog
    private lateinit var addressDataChangedListener: AddressChangedListener
    private lateinit var farmerDataChangedListener: DataChangedListener
    private lateinit var farmerDataErrorListener: DataErrorListener
    private lateinit var addressDataErrorListener: DataErrorListener
    private var bankList: ArrayList<com.javaindoku.yotaniniaga.model.profile.bank.Data> = ArrayList()
    private var editMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_farmer)
        loadingDialog = loadingDialog(this)
        initToolbar()
        val adapter = PaymentFragmentAdapter(supportFragmentManager)
        val farmerDataFragment = FarmerDataFormFragment()
        val farmerAddressFragment = FarmerAddressFormFragment()
        addressDataErrorListener = farmerAddressFragment
        farmerDataErrorListener = farmerDataFragment
        farmer.mobile = intent.getStringExtra("mobile")
        adapter.addFragment(farmerDataFragment, getString(R.string.profile))
        adapter.addFragment(farmerAddressFragment, getString(R.string.address))
        binding.viewPagerTransaction.offscreenPageLimit = 1
        binding.viewPagerTransaction.adapter = adapter
        binding.tabAddFarmer.setupWithViewPager(binding.viewPagerTransaction)
        if (!intent.getStringExtra("farmerId").isNullOrEmpty()){
            loadFarmerData(intent.getStringExtra("farmerId").toString())
            editMode = intent.getBooleanExtra("editMode", false)
        }
        viewModel.getBank()
        observeViewModel()
    }

    private fun loadFarmerData(id: String) {
        viewModel.fetchPetaniById(
            prefHelper.getStringFromShared(SharedPrefKeys.TOKEN).toString(),
            prefHelper.getStringFromShared(SharedPrefKeys.USER_ID).toString(),
            id
        )
    }

    private fun initToolbar() {
        binding.lytToolbar.lytNotificationToolbarHome.visibility = View.GONE
        binding.lytToolbar.imgSearchToolbarHome.visibility = View.GONE
        binding.lytToolbar.imgBackToolbarHome.setOnSafeClickListener {
            onBackPressed()
        }
        if (!editMode) {
            binding.lytToolbar.lblTitleToolbarHome.text = getString(R.string.add_farmer_tab)
        } else {
            binding.lytToolbar.lblTitleToolbarHome.text = getString(R.string.edit_farmer_tab)
        }
    }

    fun delegateLoader(): Dialog {
        return loadingDialog
    }

    fun setAddressDataChangeNotifier(dataChangedListener: AddressChangedListener) {
        this.addressDataChangedListener = dataChangedListener
        if (intent.getStringExtra(FARMER_ID).isNullOrEmpty())
            dataChangedListener.onAddressChanged(farmer)
    }

    fun setFarmerDataChangeNotifier(dataChangedListener: DataChangedListener) {
        this.farmerDataChangedListener = dataChangedListener
        if (intent.getStringExtra(FARMER_ID).isNullOrEmpty())
            dataChangedListener.onDataChanged(farmer)
    }

    override fun onClick(view: View?) {
        var currentFragment = binding.viewPagerTransaction.currentItem
        if (view?.id == R.id.btnConfirmationPositive) {
            binding.viewPagerTransaction.currentItem = currentFragment + 1
        } else {
            binding.viewPagerTransaction.currentItem = currentFragment - 1
        }
    }

    fun submit() {
        if (!formDataIsValid()) {
            farmerDataErrorListener.onError(farmer)
            val currentFragment = binding.viewPagerTransaction.currentItem
            binding.viewPagerTransaction.currentItem = currentFragment - 1
            return
        }

        if (!formAddressIsValid()) {
            addressDataErrorListener.onError(farmer)
            return
        }

        if (!editMode) {
            viewModel.addFarmer(
                prefHelper.getStringFromShared(SharedPrefKeys.TOKEN).toString(),
                prefHelper.getStringFromShared(SharedPrefKeys.USER_ID).toString(),
                prefHelper.getStringFromShared(SharedPrefKeys.KOPERASI_ID).toString(),
                farmer,
                convertUriToBitmap(imageUri)
            )
        } else {
            viewModel.updateFarmer(
                prefHelper.getStringFromShared(SharedPrefKeys.TOKEN).toString(),
                prefHelper.getStringFromShared(SharedPrefKeys.USER_ID).toString(),
                prefHelper.getStringFromShared(SharedPrefKeys.KOPERASI_ID).toString(),
                farmer,
                convertUriToBitmap(imageUri)
            )
        }
    }

    private fun formAddressIsValid(): Boolean {
        return !farmer.alamat.isNullOrBlank() && !farmer.rt.isNullOrBlank() && !farmer.rw.isNullOrBlank()
                && !farmer.kodePos.isNullOrBlank()
    }

    private fun formDataIsValid(): Boolean {
        return !farmer.nama.isNullOrBlank() && !farmer.mobile.isNullOrBlank() && !farmer.akunBank.isNullOrBlank()
                && !farmer.noRekening.isNullOrBlank()
    }

    private fun convertUriToBitmap(uri: String): String {
        if (uri.isEmpty()) return ""
        val imageBitmap: Bitmap = SiliCompressor.with(this).getCompressBitmap(uri)
        return encodeImage(imageBitmap)
    }

    private fun encodeImage(bm: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.NO_WRAP)
    }

    fun observeViewModel() {
        viewModel.addFarmerResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                var message = getString(R.string.farmer_added)
                if (editMode) {
                    message = getString(R.string.profile_updated)
                }
                CustomDialog.dialogOneButton(
                    this,
                    true,
                    getString(R.string.success),
                    message,
                    R.drawable.ic_dialog_success,
                    true,
                    getString(R.string.srOk),
                    {activity -> finish()},
                ).show()
            }
        })

        viewModel.farmerResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                val farmerResult = it.data?.data
                if (farmerResult != null) {
                    farmer = farmerResult
                    farmer.mobile = intent.getStringExtra("mobile")
                    if(this::addressDataChangedListener.isInitialized) addressDataChangedListener.onAddressChanged(farmerResult)
                    if(this::farmerDataChangedListener.isInitialized) farmerDataChangedListener.onDataChanged(farmerResult)
                }
            }
        })

        viewModel.bankResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                val bankResult = it.data?.data
                if (bankResult != null) {
                    bankList.clear()
                    bankList.addAll(bankResult)
                    farmerDataChangedListener.onBankListChanged(bankList)
                }
            }
        })
    }

    fun delegateFarmer(): Data {
        return farmer
    }
}