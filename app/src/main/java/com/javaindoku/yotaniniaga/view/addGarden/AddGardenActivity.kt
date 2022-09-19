package com.javaindoku.yotaniniaga.view.addGarden

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseActivity
import com.javaindoku.yotaniniaga.databinding.ActivityAddGardenBinding
import com.javaindoku.yotaniniaga.di.factory.ViewModelFactory
import com.javaindoku.yotaniniaga.model.gardenPetani.CertificateGarden
import com.javaindoku.yotaniniaga.model.gardenPetani.DocumentGarden
import com.javaindoku.yotaniniaga.model.gardenPetani.GardenDataSchema
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.utilities.ConstValue.EDIT_MODE
import com.javaindoku.yotaniniaga.utilities.ConstValue.FARMER_ID
import com.javaindoku.yotaniniaga.utilities.ConstValue.GARDEN_ID
import com.javaindoku.yotaniniaga.utilities.SharedPrefKeys
import com.javaindoku.yotaniniaga.utilities.dialog.CustomDialog
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import com.javaindoku.yotaniniaga.view.addGarden.fragment.*
import com.javaindoku.yotaniniaga.view.addGarden.listener.CertificateListChangedListener
import com.javaindoku.yotaniniaga.view.addGarden.listener.DataChangedListener
import com.javaindoku.yotaniniaga.view.addGarden.listener.DocumentListChangedListener
import com.javaindoku.yotaniniaga.view.addGarden.listener.dataErrorListener
import com.javaindoku.yotaniniaga.view.report.adapter.PaymentFragmentAdapter
import com.javaindoku.yotaniniaga.viewmodel.AddGardenViewModel
import dagger.android.AndroidInjection
import javax.inject.Inject

class AddGardenActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding : ActivityAddGardenBinding
    @Inject
    lateinit var viewModel: AddGardenViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private var garden = GardenDataSchema()
    private lateinit var loadingDialog: Dialog
    private lateinit var addressDataChangedListener: DataChangedListener
    private lateinit var gardenDataChangedListener: DataChangedListener
    private lateinit var documentDataChangedListener: DocumentListChangedListener
    private lateinit var certificateListDataChangedListener: CertificateListChangedListener
    private lateinit var certificateDataChangedListener: DataChangedListener
    private lateinit var gardenDataErrorListener: dataErrorListener
    private lateinit var addressDataErrorListener: dataErrorListener
    private lateinit var certificateListDataErrorListener: dataErrorListener
    //private lateinit var certificateDataErrorListener: dataErrorListener
    private var documentList = mutableListOf<DocumentGarden>()
    private var certificateList = mutableListOf<CertificateGarden>()
    private var isKoperasi = false
    private var farmerId = ""
    var editMode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_garden)
        loadingDialog = loadingDialog(this)
        if (!intent.getStringExtra(EDIT_MODE).isNullOrEmpty()){
            editMode = intent.getStringExtra(EDIT_MODE).toString()
        }
        initToolbar()
        val adapter = PaymentFragmentAdapter(supportFragmentManager)
        val addressGardenFragment = AddressGardenFragment()
        val gardenAddFragment = GardenAddFragment()
        val documentLegalityFragment = DocumentLegalityFragment()
        val certificateGardenListFragment = CertificateGardenListFragment()
        //val certificateGardenFragment = CertificateGardenFragment()
        gardenDataErrorListener = addressGardenFragment
        addressDataErrorListener = gardenAddFragment
        certificateListDataChangedListener = certificateGardenListFragment
        adapter.addFragment(addressGardenFragment, getString(R.string.address))
        adapter.addFragment(gardenAddFragment, getString(R.string.garden))
        adapter.addFragment(documentLegalityFragment, getString(R.string.legality_document))
        adapter.addFragment(certificateGardenListFragment, getString(R.string.certificate_garden))
        binding.viewPagerTransaction.offscreenPageLimit = 1
        binding.viewPagerTransaction.adapter = adapter
        binding.tabAddGarden.setupWithViewPager(binding.viewPagerTransaction)
        if (!intent.getStringExtra(GARDEN_ID).isNullOrEmpty())
            loadGardenData(intent.getStringExtra(GARDEN_ID).toString())
        if (!intent.getStringExtra(FARMER_ID).isNullOrEmpty())
            farmerId = intent.getStringExtra(FARMER_ID).toString()
        observeViewModel()
    }

    private fun loadGardenData(id: String) {
        viewModel.fetchGardenById(
            prefHelper.getStringFromShared(SharedPrefKeys.TOKEN).toString(),
            prefHelper.getStringFromShared(SharedPrefKeys.USER_ID).toString(),
            id
        )
    }

    fun loadCertificateDocument() {
        if(intent.getStringExtra(GARDEN_ID).isNullOrEmpty()) {
            documentDataChangedListener.onDataChanged(ArrayList())
            return
        }
        viewModel.fetchGardenCertificates(
            prefHelper.getStringFromShared(SharedPrefKeys.TOKEN).toString(),
            prefHelper.getStringFromShared(SharedPrefKeys.USER_ID).toString(),
            intent.getStringExtra(GARDEN_ID),
            "nomor",
            "asc"
        )
    }

    fun loadCertificateList() {
        if(intent.getStringExtra(GARDEN_ID).isNullOrEmpty()) {
            certificateListDataChangedListener.onDataChanged(ArrayList())
            return
        }
        viewModel.fetchCertificatesList(
            prefHelper.getStringFromShared(SharedPrefKeys.TOKEN).toString(),
            prefHelper.getStringFromShared(SharedPrefKeys.USER_ID).toString(),
            intent.getStringExtra(GARDEN_ID),
            "nomor",
            "asc"
        )
    }

    private fun initToolbar() {
        //binding.lytToolbar.lblTitleToolbarHome.text = getString(R.string.add_garden_tab)
        binding.lytToolbar.lytNotificationToolbarHome.visibility = View.GONE
        binding.lytToolbar.imgSearchToolbarHome.visibility = View.GONE
        binding.lytToolbar.imgBackToolbarHome.setOnSafeClickListener {
            onBackPressed()
        }
        when (editMode) {
            "false" -> {
                binding.lytToolbar.lblTitleToolbarHome.text = getString(R.string.view_garden)
            }
            "true" -> {
                binding.lytToolbar.lblTitleToolbarHome.text = getString(R.string.edit_garden)
            }
            else -> {
                binding.lytToolbar.lblTitleToolbarHome.text = getString(R.string.add_garden_tab)
            }
        }
    }

    public fun delegateGarden(): GardenDataSchema {
        return garden
    }

    fun delegateLoader(): Dialog {
        return loadingDialog
    }

    fun delegateDocumentList(): MutableList<DocumentGarden> {
        return documentList
    }

    fun delegateCertificateList(): MutableList<CertificateGarden> {
        return certificateList
    }

    fun setAddressDataChangeNotifier(dataChangedListener: DataChangedListener) {
        this.addressDataChangedListener = dataChangedListener
        if (intent.getStringExtra(GARDEN_ID).isNullOrEmpty())
            dataChangedListener.onDataChanged(garden)
    }

    fun setGardenDataChangeNotifier(dataChangedListener: DataChangedListener) {
        this.gardenDataChangedListener = dataChangedListener
        if (intent.getStringExtra(GARDEN_ID).isNullOrEmpty())
            dataChangedListener.onDataChanged(garden)
    }

    fun setDocumentDataChangeNotifier(dataChangedListener: DocumentListChangedListener) {
        this.documentDataChangedListener = dataChangedListener
        if (intent.getStringExtra(GARDEN_ID).isNullOrEmpty())
            dataChangedListener.onDataChanged(documentList)
    }

    fun setCertificateListDataChangeNotifier(dataChangedListener: CertificateListChangedListener) {
        this.certificateListDataChangedListener = dataChangedListener
        if (intent.getStringExtra(GARDEN_ID).isNullOrEmpty())
            dataChangedListener.onDataChanged(certificateList)
    }

    fun setCertificateDataChangeNotifier(dataChangedListener: DataChangedListener) {
        this.certificateDataChangedListener = dataChangedListener
        if (intent.getStringExtra(GARDEN_ID).isNullOrEmpty())
            dataChangedListener.onDataChanged(garden)
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
        if (intent.getStringExtra(GARDEN_ID).isNullOrEmpty()) {
            viewModel.addGarden(
                prefHelper.getStringFromShared(SharedPrefKeys.TOKEN).toString(),
                prefHelper.getStringFromShared(SharedPrefKeys.USER_ID).toString(),
                if (farmerId.isNullOrEmpty()) prefHelper.getStringFromShared(SharedPrefKeys.PETANI_ID).toString() else farmerId,
                garden,
                documentList,
                certificateList
            )
        } else {
            viewModel.updateGarden(
                prefHelper.getStringFromShared(SharedPrefKeys.TOKEN).toString(),
                prefHelper.getStringFromShared(SharedPrefKeys.USER_ID).toString(),
                if (farmerId.isNullOrEmpty()) prefHelper.getStringFromShared(SharedPrefKeys.PETANI_ID).toString() else farmerId,
                garden,
                documentList,
                certificateList
            )
        }
    }

    fun observeViewModel() {
        viewModel.addGardenResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                CustomDialog.dialogOneButton(
                    this,
                    true,
                    getString(R.string.success),
                    if (intent.getStringExtra(GARDEN_ID).isNullOrEmpty()) getString(R.string.farm_added) else getString(R.string.farm_updated),
                    R.drawable.ic_dialog_success,
                    true,
                    getString(R.string.srOk),
                    {activity -> finish()},
                ).show()
            }
        })

        viewModel.gardenCertificateListResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                documentList.clear()
                documentList.addAll(it.data?.data!!)
                if(this::documentDataChangedListener.isInitialized)
                    documentDataChangedListener.onDataChanged(it.data?.data!!)
            }
        })

        viewModel.certificateListResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                certificateList.clear()
                certificateList.addAll(it.data?.data!!)
                if(this::certificateListDataChangedListener.isInitialized)
                    certificateListDataChangedListener.onDataChanged(it.data?.data!!)
            }
        })

        viewModel.gardenResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                val gardenResult = it.data?.data?.get(0)
                if (gardenResult != null) {
                    garden = gardenResult
                    if(this::addressDataChangedListener.isInitialized) addressDataChangedListener.onDataChanged(gardenResult)
                    if(this::gardenDataChangedListener.isInitialized) gardenDataChangedListener.onDataChanged(gardenResult)
                    if(this::certificateDataChangedListener.isInitialized) certificateDataChangedListener.onDataChanged(gardenResult)
                }
            }
        })
    }
}