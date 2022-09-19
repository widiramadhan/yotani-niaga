package com.javaindoku.yotaniniaga.view.detailGarden

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseActivity
import com.javaindoku.yotaniniaga.databinding.ActivityGardenViewDetailBinding
import com.javaindoku.yotaniniaga.databinding.ActivityProfilePetaniDetailBinding
import com.javaindoku.yotaniniaga.model.gardenPetani.CertificateGarden
import com.javaindoku.yotaniniaga.model.gardenPetani.DocumentGarden
import com.javaindoku.yotaniniaga.model.gardenPetani.GardenDataSchema
import com.javaindoku.yotaniniaga.model.profilePetaniDetail.Data
import com.javaindoku.yotaniniaga.model.profilePetaniDetail.ProfilePetaniDetail
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.utilities.*
import com.javaindoku.yotaniniaga.utilities.StringFormat.stringCheck
import com.javaindoku.yotaniniaga.utilities.dialog.CustomDialog
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import com.javaindoku.yotaniniaga.view.addGarden.adapter.CertificatetListAdapter
import com.javaindoku.yotaniniaga.view.addGarden.adapter.DocumentListAdapter
import com.javaindoku.yotaniniaga.view.delivery.dialog.AddDocumentDialog
import com.javaindoku.yotaniniaga.view.detailGarden.adapter.CertificatetListViewAdapter
import com.javaindoku.yotaniniaga.view.detailGarden.adapter.DocumentListViewAdapter
import com.javaindoku.yotaniniaga.view.editprofilepetani.EditProfilePetaniActivity
import com.javaindoku.yotaniniaga.viewmodel.AddGardenViewModel
import com.javaindoku.yotaniniaga.viewmodel.ProfilePetaniDetailViewModel
import com.kotlinpermissions.KotlinPermissions
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.fragment_farmer_address.*
import kotlinx.android.synthetic.main.toolbar_home.view.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class GardenViewDetailActivity : BaseActivity(), DocumentListViewAdapter.ActionListener, CertificatetListViewAdapter.ActionListener,
    OnMapReadyCallback {
    private lateinit var binding: ActivityGardenViewDetailBinding

    @Inject
    lateinit var viewModel: AddGardenViewModel
    private var garden = GardenDataSchema()
    private var documentList = mutableListOf<DocumentGarden>()
    private var certificateList = mutableListOf<CertificateGarden>()
    private var farmerId = ""
    private lateinit var loadingDialog: Dialog
    private lateinit var documentListAdapter: DocumentListViewAdapter
    private lateinit var certificateListAdapter: CertificatetListViewAdapter
    private var permissionRequestCode = 5
    private lateinit var searchAdapter: CustomArrayAdapter
    private lateinit var locationProvider: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap
    private lateinit var geoCoder: Geocoder
    private lateinit var markedCoordinate: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_garden_view_detail)
        initToolbar()
        loadingDialog = loadingDialog(this)
        searchAdapter = CustomArrayAdapter(this)
        if (!intent.getStringExtra(ConstValue.GARDEN_ID).isNullOrEmpty()) {
            loadGardenData(intent.getStringExtra(ConstValue.GARDEN_ID).toString())
            loadDocument(intent.getStringExtra(ConstValue.GARDEN_ID).toString())
            loadCertificate(intent.getStringExtra(ConstValue.GARDEN_ID).toString())
        }
        if (!intent.getStringExtra(ConstValue.FARMER_ID).isNullOrEmpty()) {
            farmerId = intent.getStringExtra(ConstValue.FARMER_ID).toString()
        }
        initMap()
        binding.edtxMapSearch.setAdapter(searchAdapter)
        binding.edtxMapSearch.setOnItemClickListener(AdapterView.OnItemClickListener { adapterView, view, position, id ->
            var address = adapterView.getItemAtPosition(position) as Address
            edtxMapSearch.setText(address.getAddressLine(0))
            moveCameraToCoordinates(LatLng(address.latitude, address.longitude))
            garden.latitude = address.latitude.toString()
            garden.longitude = address.longitude.toString()
        })
        observeViewModel()
    }

    private fun initToolbar() {
        binding.lytToolbar.lblTitleToolbarHome.text = getString(R.string.view_garden)
        binding.lytToolbar.imgSearchToolbarHome.visibility = View.GONE
        binding.lytToolbar.imgBackToolbarHome.setOnSafeClickListener {
            onBackPressed()
        }
    }

    private fun loadGardenData(id: String) {
        viewModel.fetchGardenById(
            prefHelper.getStringFromShared(SharedPrefKeys.TOKEN).toString(),
            prefHelper.getStringFromShared(SharedPrefKeys.USER_ID).toString(),
            id
        )
    }

    fun loadDocument(id: String) {
        viewModel.fetchGardenCertificates(
            prefHelper.getStringFromShared(SharedPrefKeys.TOKEN).toString(),
            prefHelper.getStringFromShared(SharedPrefKeys.USER_ID).toString(),
            id,
            "nomor",
            "asc"
        )
    }

    fun loadCertificate(id: String) {
        viewModel.fetchCertificatesList(
            prefHelper.getStringFromShared(SharedPrefKeys.TOKEN).toString(),
            prefHelper.getStringFromShared(SharedPrefKeys.USER_ID).toString(),
            id,
            "nomor",
            "asc"
        )
    }

    private fun observeViewModel(){
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
                    bindView(garden)
                }
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
                bindListDocument(documentList)
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
                bindListCertificate(certificateList)
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun bindView(data: GardenDataSchema?) {
        binding.txtAddress.setText(stringCheck(data?.alamat, "") + ", " +
                                    stringCheck(data?.kelurahanName, "") + ", "+
                                    stringCheck(data?.kecamatanName, "") + ", " +
                                    stringCheck(data?.kabupatenKotaName, "") + ", " +
                                    stringCheck(data?.provinsiName, "") + ", " +
                                    stringCheck(data?.kodePos, ""))
        binding.txtGardenArea.setText(stringCheck(data?.luasKebun, "") + " HA")
        binding.txtYearPlants.setText(stringCheck(data?.tahunTanamId, ""))
        binding.txtTotalTree.setText(stringCheck(data?.jumlahPohon, ""))
        binding.txtTypeOfSeeds.setText(stringCheck(data?.jenisBibitName, ""))
        binding.txtlandConditions.setText(stringCheck(data?.statusLahanName, ""))
        binding.txtPotensialProduction.setText(stringCheck(data?.potensiProduksi, "") + " Ton/Bulan")
        binding.txtCertificateType.setText(stringCheck(data?.sertifikasiName, ""))
        binding.txtcertificateNomor.setText(stringCheck(data?.sertifikasiNumber, ""))
        binding.txtFromDate.setText(stringCheck(data?.sertifikasiDari, ""))
        binding.txtToDate.setText(stringCheck(data?.sertifikasiSampai, ""))
        CustomProfile.showRemoteImageUsingGlide(this, binding.imgGarden, data?.foto.toString(), R.drawable.bg_rectangle_white_black)
        CustomProfile.showRemoteImageUsingGlide(this, binding.imgCertificate, data?.fotoSertifikasi.toString(), R.drawable.bg_rectangle_white_black)
        initMap()
    }

    private fun bindListDocument(documentList: List<DocumentGarden>) {
        binding.rcyListDocument.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        documentListAdapter = DocumentListViewAdapter(this, ArrayList(), this)
        binding.rcyListDocument.adapter = documentListAdapter
        documentListAdapter.refreshList(documentList)
    }

    private fun bindListCertificate(certificateList: List<CertificateGarden>) {
        binding.rcyListCertificate.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        certificateListAdapter = CertificatetListViewAdapter(this, ArrayList(), this)
        binding.rcyListCertificate.adapter = certificateListAdapter
        certificateListAdapter.refreshList(certificateList)
    }

    override fun onEditClick(documentGarden: DocumentGarden) {}

    override fun onDeleteClick(documentGarden: Int) {}

    override fun onClick(documentGarden: DocumentGarden) {}

    override fun onEditClickCertificate(certificateGarden: CertificateGarden) {}

    override fun onDeleteClickCertificate(certificateGarden: Int) {}

    override fun onClickCertificate(certificateGarden: CertificateGarden) {}

    private fun initMap() {
        locationProvider = LocationServices.getFusedLocationProviderClient(this)
        if (!checkLocationPermission()) return;
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        locationProvider.lastLocation.addOnSuccessListener {
            if (garden.longitude.isNullOrEmpty() || garden.latitude.isNullOrEmpty()) {
                garden.longitude = it.longitude .toString()
                garden.latitude = it.latitude.toString()
                markedCoordinate = LatLng(it.latitude, it.longitude)
            } else {
                markedCoordinate = LatLng(garden.latitude.toString().toDouble(), garden.longitude.toString().toDouble())
            }
            mapFragment.getMapAsync(this)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkLocationPermission() : Boolean {
        if (ContextCompat.checkSelfPermission(this
                , android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), permissionRequestCode)
            return false;
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        geoCoder = Geocoder(this, Locale.getDefault())
        if (googleMap != null) {
            this.googleMap = googleMap
        }
        moveCameraToCoordinates(markedCoordinate)
    }

    private fun moveCameraToCoordinates(coordinates: LatLng) {
        var myLocation = CameraUpdateFactory.newLatLngZoom(coordinates, ConstValue.MAP_ZOOM_VALUE)
        this.googleMap.animateCamera(myLocation)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionRequestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initMap()
            }
        }
    }
}