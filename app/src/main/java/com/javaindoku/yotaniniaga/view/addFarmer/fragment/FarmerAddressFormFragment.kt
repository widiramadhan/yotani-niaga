package com.javaindoku.yotaniniaga.view.addFarmer.fragment

import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseFragment
import com.javaindoku.yotaniniaga.databinding.FragmentAddressGardenBinding
import com.javaindoku.yotaniniaga.di.factory.ViewModelFactory
import com.javaindoku.yotaniniaga.model.profile.provinsi.Data
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.utilities.ConstValue
import com.javaindoku.yotaniniaga.utilities.CustomArrayAdapter
import com.javaindoku.yotaniniaga.utilities.CustomSpinnerAdapter
import com.javaindoku.yotaniniaga.view.addFarmer.AddFarmerActivity
import com.javaindoku.yotaniniaga.view.addFarmer.listener.AddressChangedListener
import com.javaindoku.yotaniniaga.view.addFarmer.listener.DataErrorListener
import com.javaindoku.yotaniniaga.viewmodel.AddFarmerViewModel
import kotlinx.android.synthetic.main.fragment_farmer_address.*
import java.util.*
import javax.inject.Inject

class FarmerAddressFormFragment : BaseFragment(), OnMapReadyCallback,
    AdapterView.OnItemSelectedListener, AddressChangedListener, DataErrorListener {
    private lateinit var binding: FragmentAddressGardenBinding
    private var permissionRequestCode = 5
    private lateinit var parent:AddFarmerActivity
    @Inject
    lateinit var viewModel: AddFarmerViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var loadingDialog: Dialog
    private lateinit var searchAdapter: CustomArrayAdapter
    private lateinit var locationProvider: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap
    private lateinit var geoCoder: Geocoder
    private lateinit var markedCoordinate: LatLng
    private lateinit var nextButtonListener: View.OnClickListener
    private lateinit var listProvinsi: MutableList<Data>
    private lateinit var listKabupatenKota: MutableList<com.javaindoku.yotaniniaga.model.profile.kabupaten.Data>
    private lateinit var listKecamatan: MutableList<com.javaindoku.yotaniniaga.model.profile.kecamatan.Data>
    private lateinit var listKelurahan: MutableList<com.javaindoku.yotaniniaga.model.profile.kelurahan.Data>
    private lateinit var provinsiAdapter: CustomSpinnerAdapter<Data>
    private lateinit var kabupatenAdapter: CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.kabupaten.Data>
    private lateinit var kecamatanAdapter: CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.kecamatan.Data>
    private lateinit var kelurahanAdapter: CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.kelurahan.Data>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_address_garden, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parent = activity as AddFarmerActivity
        parent.setAddressDataChangeNotifier(this)
        loadingDialog = parent.delegateLoader()
        searchAdapter = CustomArrayAdapter(requireContext())
        initMap()
        binding.spinnerProvinsi.onItemSelectedListener = this
        binding.spinnerProvinsi.setTitle(getString(R.string.srSelectProvince))
        binding.spinnerKabupaten.onItemSelectedListener = this
        binding.spinnerKabupaten.setTitle(getString(R.string.srSelectCity))
        binding.spinnerKecamatan.onItemSelectedListener = this
        binding.spinnerKabupaten.setTitle(getString(R.string.srSelectDistrict))
        binding.spinnerKelurahan.onItemSelectedListener = this
        binding.spinnerKelurahan.setTitle(getString(R.string.srSelectSubDistrict))
        binding.spinnerProvinsi.setOnSearchTextChangedListener {
            for (i in 0 until listProvinsi.size) {
                if(listProvinsi[i].provinsiName.equals(binding.spinnerProvinsi.selectedItem.toString())){
                    break
                }
            }
        }
        binding.spinnerKabupaten.setOnSearchTextChangedListener {
            for (i in 0 until listKabupatenKota.size) {
                if(listKabupatenKota[i].kabupatenKotaName.equals(binding.spinnerKabupaten.selectedItem.toString())){
                    break
                }
            }
        }
        binding.spinnerKecamatan.setOnSearchTextChangedListener {
            for (i in 0 until listKecamatan.size) {
                if(listKecamatan[i].kecamatanName.equals(binding.spinnerKecamatan.selectedItem.toString())){
                    break
                }
            }
        }
        binding.spinnerKelurahan.setOnSearchTextChangedListener {
            for (i in 0 until listKelurahan.size) {
                if(listKelurahan[i].kelurahanDesaName.equals(binding.spinnerKelurahan.selectedItem.toString())){
                    break
                }
            }
        }
        binding.edtxMapSearch.setAdapter(searchAdapter)
        binding.edtxMapSearch.setOnItemClickListener { adapterView, _, position, _ ->
            val address = adapterView.getItemAtPosition(position) as Address
            edtxMapSearch.setText(address.getAddressLine(0))
            moveCameraToCoordinates(LatLng(address.latitude, address.longitude))
            parent.delegateFarmer().latitude = address.latitude.toString()
            parent.delegateFarmer().longitude = address.longitude.toString()
        }
        binding.btnConfirmationPositive.setOnClickListener{
            parent.submit()
        }
        binding.btnConfirmationNegative.setOnClickListener{
            nextButtonListener.onClick(it)
        }
        binding.edtKTPAlamat.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateFarmer().alamat = p0.toString()
            }
        })
        binding.edtKTPRT.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateFarmer().rt = p0.toString()
            }
        })
        binding.edtKTPRW.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateFarmer().rw = p0.toString()
            }
        })
        binding.edtKTPKodePos.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateFarmer().kodePos = p0.toString()
            }
        })
        observeViewModel()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        nextButtonListener = context as View.OnClickListener
    }

    private fun observeViewModel() {
        viewModel.provinsiResult.observe(requireActivity(), Observer {
            when (it.status) {
                ApiResult.Status.LOADING -> {
                    loadingDialog.show()
                }
                ApiResult.Status.ERROR -> {
                    loadingDialog.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    loadingDialog.dismiss()
                    val dataProvinsi = it.data!!.data!!
                    listProvinsi = dataProvinsi.toMutableList()
                    provinsiAdapter = CustomSpinnerAdapter(
                        requireActivity(),
                        dataProvinsi
                    )
                    binding.spinnerProvinsi.adapter = provinsiAdapter
                }
            }
        })
    }

    private fun getProvinsi() {
        viewModel.getProvinsi()
        viewModel.provinsiResult.observe(requireActivity(), Observer {
            when (it.status) {
                ApiResult.Status.LOADING -> {
                    loadingDialog.show()
                }
                ApiResult.Status.ERROR -> {
                    loadingDialog.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    loadingDialog.dismiss()
                    val dataProvinsi = it.data!!.data!!
                    listProvinsi = dataProvinsi.toMutableList()
                    provinsiAdapter = CustomSpinnerAdapter(
                        requireActivity(),
                        dataProvinsi
                    )
                    binding.spinnerProvinsi.adapter = provinsiAdapter
                    if (!parent.delegateFarmer().provinsiId.isNullOrEmpty()) {
                        binding.spinnerProvinsi.setSelection(provinsiAdapter.getItemPositionById(parent.delegateFarmer().provinsiId.toString()))
                    }
                }
            }
        })
    }

    private fun getKabupaten(provinsiId: Int) {
        viewModel.getkabupaten(provinsiId)
        viewModel.kabupatenResult.observe(requireActivity(), Observer {
            when (it.status) {
                ApiResult.Status.LOADING -> {
                    loadingDialog.show()
                }
                ApiResult.Status.ERROR -> {
                    loadingDialog.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    loadingDialog.dismiss()
                    val dataKabupaten = it.data!!.data!!
                    listKabupatenKota = dataKabupaten.toMutableList()
                    kabupatenAdapter = CustomSpinnerAdapter(
                        requireActivity(),
                        dataKabupaten
                    )
                    binding.spinnerKabupaten.adapter = kabupatenAdapter
                    if (!parent.delegateFarmer().kabupatenKotaId.isNullOrEmpty()) {
                        binding.spinnerKabupaten.setSelection(kabupatenAdapter.getItemPositionById(parent.delegateFarmer().kabupatenKotaId.toString()))
                    }
                }
            }
        })
    }

    private fun getKecamatan(kabupatenId: Int) {
        viewModel.getKecamatan(kabupatenId)
        //val loadingDialog = loadingDialog(requireActivity())
        viewModel.kecamatanResult.observe(requireActivity(), Observer {
            when (it.status) {
                ApiResult.Status.LOADING -> {
                    loadingDialog.show()
                }
                ApiResult.Status.ERROR -> {
                    loadingDialog.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    loadingDialog.dismiss()
                    val dataKecamatan = it.data!!.data!!
                    listKecamatan = dataKecamatan.toMutableList()
                    kecamatanAdapter = CustomSpinnerAdapter(
                        requireActivity(),
                        dataKecamatan
                    )
                    binding.spinnerKecamatan.adapter = kecamatanAdapter
                    if (!parent.delegateFarmer().kecamatanId.isNullOrEmpty()) {
                        binding.spinnerKecamatan.setSelection(kecamatanAdapter.getItemPositionById(parent.delegateFarmer().kecamatanId.toString()))
                    }
                }
            }
        })
    }

    private fun getKelurahan(kecamatanId: Int) {
        viewModel.getKelurahan(kecamatanId)
        viewModel.kelurahanResult.observe(requireActivity(), Observer {
            when (it.status) {
                ApiResult.Status.LOADING -> {
                    loadingDialog.show()
                }
                ApiResult.Status.ERROR -> {
                    loadingDialog.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    loadingDialog.dismiss()
                    val dataKelurahan = it.data!!.data!!
                    listKelurahan = dataKelurahan.toMutableList()
                    kelurahanAdapter = CustomSpinnerAdapter(
                        requireActivity(),
                        dataKelurahan
                    )
                    binding.spinnerKelurahan.adapter = kelurahanAdapter
                    if (!parent.delegateFarmer().kelurahanId.isNullOrEmpty()) {
                        binding.spinnerKelurahan.setSelection(kelurahanAdapter.getItemPositionById(parent.delegateFarmer().kelurahanId.toString()))
                    }
                }
            }
        })
    }

    private fun initMap() {
        locationProvider = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (!checkLocationPermission()) return
        val mapFragment = childFragmentManager.findFragmentById(R.id.map)
                as SupportMapFragment
        locationProvider.lastLocation.addOnSuccessListener {
            if (parent.delegateFarmer().longitude.isNullOrEmpty() || parent.delegateFarmer().latitude.isNullOrEmpty()) {
                parent.delegateFarmer().longitude = it.longitude .toString()
                parent.delegateFarmer().latitude = it.latitude.toString()
                markedCoordinate = LatLng(it.latitude, it.longitude)
            } else {
                markedCoordinate = LatLng(parent.delegateFarmer().latitude.toString().toDouble(), parent.delegateFarmer().longitude.toString().toDouble())
            }
            mapFragment.getMapAsync(this)
        }
    }

    private fun checkLocationPermission() : Boolean {
        return if (ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), permissionRequestCode)
            false
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        geoCoder = Geocoder(requireActivity(), Locale.getDefault())
        if (googleMap != null) {
            this.googleMap = googleMap
        }
        moveCameraToCoordinates(markedCoordinate)
    }

    private fun moveCameraToCoordinates(coordinates: LatLng) {
        val myLocation = CameraUpdateFactory.newLatLngZoom(coordinates, ConstValue.MAP_ZOOM_VALUE)
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

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        when (p0!!.id) {
            R.id.spinnerProvinsi -> {
                parent.delegateFarmer().provinsiId = provinsiAdapter.items[p2].provinsiId.toString()
                getKabupaten(provinsiAdapter.items[p2].provinsiId!!.toInt())
            }

            //Kabupaten Kota
            R.id.spinnerKabupaten -> {
                parent.delegateFarmer().kabupatenKotaId = kabupatenAdapter.items[p2].kabupatenKotaId.toString()
                getKecamatan(kabupatenAdapter.items[p2].kabupatenKotaId!!.toInt())
            }

            //Kecamatan Kota
            R.id.spinnerKecamatan -> {
                parent.delegateFarmer().kecamatanId = kecamatanAdapter.items[p2].kecamatanId.toString()
                getKelurahan(kecamatanAdapter.items[p2].kecamatanId!!.toInt())
            }

            //Kelurahan Kota
            R.id.spinnerKelurahan -> {
                parent.delegateFarmer().kelurahanId = kelurahanAdapter.items[p2].kelurahanDesaId.toString()
                parent.delegateFarmer().kodePos = kelurahanAdapter.items[p2].kodePos.toString()
                binding.edtKTPKodePos.setText(kelurahanAdapter.items[p2].kodePos.toString())
            }
        }
    }

    private fun bindView(farmerData: com.javaindoku.yotaniniaga.model.profilePetaniDetail.Data) {
        if (!farmerData.longitude.isNullOrEmpty() && !farmerData.latitude.isNullOrEmpty()) {
            markedCoordinate = LatLng(farmerData.latitude.toString().toDouble(), farmerData.longitude.toString().toDouble())
            if (::googleMap.isInitialized) moveCameraToCoordinates(markedCoordinate)
        }
        binding.edtKTPRT.setText(farmerData.rt)
        binding.edtKTPRW.setText(farmerData.rw)
        binding.edtKTPAlamat.setText(farmerData.alamat)
        binding.edtKTPKodePos.setText(farmerData.kodePos)
        getProvinsi()
    }

    override fun onResume() {
        super.onResume()
        getProvinsi()
    }

    override fun onAddressChanged(farmer: com.javaindoku.yotaniniaga.model.profilePetaniDetail.Data) {
        bindView(farmer)
    }

    override fun onError(profile: com.javaindoku.yotaniniaga.model.profilePetaniDetail.Data) {
        if (profile.alamat.isNullOrBlank()) {
            binding.edtKTPAlamat.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorKtpAlamat.visibility = View.VISIBLE
        } else {
            binding.edtKTPAlamat.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorKtpAlamat.visibility = View.GONE
        }

        if (profile.rt.isNullOrBlank()) {
            binding.edtKTPRT.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorKtpRt.visibility = View.VISIBLE
        } else {
            binding.edtKTPRT.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorKtpRt.visibility = View.GONE
        }

        if (profile.rw.isNullOrBlank()) {
            binding.edtKTPRW.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorKtpRw.visibility = View.VISIBLE
        } else {
            binding.edtKTPRW.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorKtpRw.visibility = View.GONE
        }

        if (profile.kodePos.isNullOrBlank()) {
            binding.edtKTPKodePos.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorKtpKodePos.visibility = View.VISIBLE
        } else {
            binding.edtKTPKodePos.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorKtpKodePos.visibility = View.GONE
        }
    }
}