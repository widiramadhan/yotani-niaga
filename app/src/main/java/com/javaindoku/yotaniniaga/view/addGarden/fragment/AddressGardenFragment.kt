package com.javaindoku.yotaniniaga.view.addGarden.fragment

import android.app.Dialog
import android.content.Context
import android.content.Intent
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
import com.javaindoku.yotaniniaga.model.gardenPetani.GardenDataSchema
import com.javaindoku.yotaniniaga.model.profile.provinsi.Data
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.utilities.ConstValue
import com.javaindoku.yotaniniaga.utilities.CustomArrayAdapter
import com.javaindoku.yotaniniaga.utilities.CustomSpinnerAdapter
import com.javaindoku.yotaniniaga.view.addGarden.AddGardenActivity
import com.javaindoku.yotaniniaga.view.addGarden.listener.DataChangedListener
import com.javaindoku.yotaniniaga.view.addGarden.listener.dataErrorListener
import com.javaindoku.yotaniniaga.viewmodel.AddGardenViewModel
import kotlinx.android.synthetic.main.fragment_farmer_address.*
import java.util.*
import javax.inject.Inject

class AddressGardenFragment : BaseFragment(), OnMapReadyCallback,
    AdapterView.OnItemSelectedListener,
    DataChangedListener, dataErrorListener {
    private lateinit var binding: FragmentAddressGardenBinding
    private var permissionRequestCode = 5
    private lateinit var parent:AddGardenActivity
    @Inject
    lateinit var viewModel: AddGardenViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var loadingDialog: Dialog
    private lateinit var searchAdapter: CustomArrayAdapter
    private lateinit var locationProvider: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap
    private lateinit var geoCoder: Geocoder
    private lateinit var markedCoordinate: LatLng
    private lateinit var nextButtonListener: View.OnClickListener
    private lateinit var listProvinsi: MutableList<com.javaindoku.yotaniniaga.model.profile.provinsi.Data>
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
        parent = activity as AddGardenActivity
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
            if (binding.spinnerProvinsi != null) {
                for (i in 0..listProvinsi.size - 1) {
                    if(listProvinsi.get(i).provinsiName.equals(binding.spinnerProvinsi.selectedItem.toString())){
                        //setBackPagetrue()
                        break
                    }

                    if(i == (listProvinsi.size-1)){
                        if(!listProvinsi.get(i).provinsiName.equals(binding.spinnerProvinsi.selectedItem.toString())){
                            //setBackPageFalse()
                        }
                    }
                }
            }else{
                //setBackPageFalse()
            }
        }
        binding.spinnerKabupaten.setOnSearchTextChangedListener {
            if (binding.spinnerKabupaten != null) {
                for (i in 0..listKabupatenKota.size - 1) {
                    if(listKabupatenKota.get(i).kabupatenKotaName.equals(binding.spinnerKabupaten.selectedItem.toString())){
                        //setBackPagetrue()
                        break
                    }

                    if(i == (listKabupatenKota.size-1)){
                        if(!listKabupatenKota.get(i).kabupatenKotaName.equals(binding.spinnerKabupaten.selectedItem.toString())){
                            //setBackPageFalse()
                        }
                    }
                }
            }else{
                //setBackPageFalse()
            }
        }
        binding.spinnerKecamatan.setOnSearchTextChangedListener {
            if (binding.spinnerKecamatan != null) {
                for (i in 0..listKecamatan.size - 1) {
                    if(listKecamatan.get(i).kecamatanName.equals(binding.spinnerKecamatan.selectedItem.toString())){
                        //setBackPagetrue()
                        break
                    }

                    if(i == (listKecamatan.size-1)){
                        if(!listKecamatan.get(i).kecamatanName.equals(binding.spinnerKecamatan.selectedItem.toString())){
                            //setBackPageFalse()
                        }
                    }
                }
            }else{
                //setBackPageFalse()
            }
        }
        binding.spinnerKelurahan.setOnSearchTextChangedListener {
            if (binding.spinnerKelurahan != null) {
                for (i in 0..listKelurahan.size - 1) {
                    if(listKelurahan.get(i).kelurahanDesaName.equals(binding.spinnerKelurahan.selectedItem.toString())){
                        //setBackPagetrue()
                        break
                    }

                    if(i == (listKelurahan.size-1)){
                        if(!listKelurahan.get(i).kelurahanDesaName.equals(binding.spinnerKelurahan.selectedItem.toString())){
                            //setBackPageFalse()
                        }
                    }
                }
            }else{
                //setBackPageFalse()
            }
        }
        binding.edtxMapSearch.setAdapter(searchAdapter)
        binding.edtxMapSearch.setOnItemClickListener(AdapterView.OnItemClickListener { adapterView, view, position, id ->
            var address = adapterView.getItemAtPosition(position) as Address
            edtxMapSearch.setText(address.getAddressLine(0))
            moveCameraToCoordinates(LatLng(address.latitude, address.longitude))
            parent.delegateGarden().latitude = address.latitude.toString()
            parent.delegateGarden().longitude = address.longitude.toString()
        })
        binding.btnConfirmationPositive.setOnClickListener{
            nextButtonListener.onClick(it)
        }
        binding.btnConfirmationNegative.setOnClickListener{
            nextButtonListener.onClick(it)
        }
        binding.edtKTPAlamat.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateGarden().alamat = p0.toString()
            }
        })
        binding.edtKTPRT.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateGarden().rt = p0.toString()
            }
        })
        binding.edtKTPRW.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateGarden().rw = p0.toString()
            }
        })
        binding.edtKTPKodePos.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateGarden().kodePos = p0.toString()
            }
        })
        editMode()
        observeViewModel()
    }

    private fun editMode() {
        if (parent.editMode == "false"){
            binding.edtKTPAlamat.isEnabled = false
            binding.edtKTPAlamat.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.shape_disabled_edittext))
            binding.edtKTPRT.isEnabled = false
            binding.edtKTPRT.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.shape_disabled_edittext))
            binding.edtKTPRW.isEnabled = false
            binding.edtKTPRW.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.shape_disabled_edittext))
            binding.spinnerProvinsi.isEnabled = false
            binding.spinnerProvinsi.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.shape_disabled_edittext))
            binding.spinnerKabupaten.isEnabled = false
            binding.spinnerKabupaten.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.shape_disabled_edittext))
            binding.spinnerKecamatan.isEnabled = false
            binding.spinnerKecamatan.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.shape_disabled_edittext))
            binding.spinnerKelurahan.isEnabled = false
            binding.spinnerKelurahan.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.shape_disabled_edittext))
            binding.edtKTPKodePos.isEnabled = false
            binding.edtKTPKodePos.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.shape_disabled_edittext))
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        nextButtonListener = context as View.OnClickListener
    }

    private fun observeViewModel() {
        viewModel.provinsiResult.observe(requireActivity(), Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            } else {
                val dataProvinsi = it.data!!.data!!
                listProvinsi = dataProvinsi.toMutableList()
                provinsiAdapter = CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.provinsi.Data>(
                    requireActivity(),
                    dataProvinsi
                )
                binding.spinnerProvinsi.adapter = provinsiAdapter
                loadingDialog.dismiss()
            }
        })
    }

    private fun getProvinsi() {
        viewModel.getProvinsi()
        viewModel.provinsiResult.observe(requireActivity(), Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            } else {
                val dataProvinsi = it.data!!.data!!
                listProvinsi = dataProvinsi.toMutableList()
                provinsiAdapter = CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.provinsi.Data>(
                    requireActivity(),
                    dataProvinsi
                )
                binding.spinnerProvinsi.adapter = provinsiAdapter
                if (!parent.delegateGarden().provinsiId.isNullOrEmpty()) {
                    binding.spinnerProvinsi.setSelection(provinsiAdapter.getItemPositionById(parent.delegateGarden().provinsiId.toString()))
                }
                loadingDialog.dismiss()
            }
        })
    }

    private fun getKabupaten(provinsiId: Int) {
        viewModel.getkabupaten(provinsiId)
        viewModel.kabupatenResult.observe(requireActivity(), Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            } else {
                val dataKabupaten = it.data!!.data!!
                listKabupatenKota = dataKabupaten.toMutableList()
                kabupatenAdapter = CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.kabupaten.Data>(
                    requireActivity(),
                    dataKabupaten
                )
                binding.spinnerKabupaten.adapter = kabupatenAdapter
                if (!parent.delegateGarden().kabupatenKotaId.isNullOrEmpty()) {
                    binding.spinnerKabupaten.setSelection(kabupatenAdapter.getItemPositionById(parent.delegateGarden().kabupatenKotaId.toString()))
                }
                loadingDialog.dismiss()
            }
        })
    }

    private fun getKecamatan(kabupatenId: Int) {
        viewModel.getKecamatan(kabupatenId)
        //val loadingDialog = loadingDialog(requireActivity())
        viewModel.kecamatanResult.observe(requireActivity(), Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            } else {
                val dataKecamatan = it.data!!.data!!
                listKecamatan = dataKecamatan.toMutableList()
                kecamatanAdapter = CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.kecamatan.Data>(
                    requireActivity(),
                    dataKecamatan
                )
                binding.spinnerKecamatan.adapter = kecamatanAdapter
                if (!parent.delegateGarden().kecamatanId.isNullOrEmpty()) {
                    binding.spinnerKecamatan.setSelection(kecamatanAdapter.getItemPositionById(parent.delegateGarden().kecamatanId.toString()))
                }
                loadingDialog.dismiss()
            }
        })
    }

    private fun getKelurahan(kecamatanId: Int) {
        viewModel.getKelurahan(kecamatanId)
        viewModel.kelurahanResult.observe(requireActivity(), Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            } else {
                val dataKelurahan = it.data!!.data!!
                listKelurahan = dataKelurahan.toMutableList()
                kelurahanAdapter = CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.kelurahan.Data>(
                    requireActivity(),
                    dataKelurahan
                )
                binding.spinnerKelurahan.adapter = kelurahanAdapter
                if (!parent.delegateGarden().kelurahanId.isNullOrEmpty()) {
                    binding.spinnerKelurahan.setSelection(kelurahanAdapter.getItemPositionById(parent.delegateGarden().kelurahanId.toString()))
                }
                loadingDialog.dismiss()
            }
        })
    }

    private fun initMap() {
        locationProvider = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (!checkLocationPermission()) return;
        val mapFragment = childFragmentManager.findFragmentById(R.id.map)
                as SupportMapFragment
        locationProvider.lastLocation.addOnSuccessListener {
            if (parent.delegateGarden().longitude.isNullOrEmpty() || parent.delegateGarden().latitude.isNullOrEmpty()) {
                parent.delegateGarden().longitude = it.longitude .toString()
                parent.delegateGarden().latitude = it.latitude.toString()
                markedCoordinate = LatLng(it.latitude, it.longitude)
            } else {
                markedCoordinate = LatLng(parent.delegateGarden().latitude.toString().toDouble(), parent.delegateGarden().longitude.toString().toDouble())
            }
            mapFragment.getMapAsync(this)
        }
    }

    private fun checkLocationPermission() : Boolean {
        if (ContextCompat.checkSelfPermission(requireActivity()
                , android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), permissionRequestCode)
            return false;
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        var iid = p0!!.getId()
        when (iid) {
            R.id.spinnerProvinsi -> {
                parent.delegateGarden().provinsiId = provinsiAdapter.items[p2].provinsiId.toString()
                getKabupaten(provinsiAdapter.items[p2].provinsiId!!.toInt())
            }

            //Kabupaten Kota
            R.id.spinnerKabupaten -> {
                parent.delegateGarden().kabupatenKotaId = kabupatenAdapter.items[p2].kabupatenKotaId.toString()
                getKecamatan(kabupatenAdapter.items[p2].kabupatenKotaId!!.toInt())
            }

            //Kecamatan Kota
            R.id.spinnerKecamatan -> {
                parent.delegateGarden().kecamatanId = kecamatanAdapter.items[p2].kecamatanId.toString()
                getKelurahan(kecamatanAdapter.items[p2].kecamatanId!!.toInt())
            }

            //Kelurahan Kota
            R.id.spinnerKelurahan -> {
                parent.delegateGarden().kelurahanId = kelurahanAdapter.items[p2].kelurahanDesaId.toString()
                parent.delegateGarden().kodePos = kelurahanAdapter.items[p2].kodePos.toString()
                binding.edtKTPKodePos.setText(kelurahanAdapter.items[p2].kodePos.toString())
            }
        }
    }

    override fun onDataChanged(gardenData: GardenDataSchema) {
        bindView(gardenData)
    }

    private fun bindView(gardenData: GardenDataSchema) {
        if (!gardenData.longitude.isNullOrEmpty() && !gardenData.latitude.isNullOrEmpty()) {
            markedCoordinate = LatLng(gardenData.latitude.toString().toDouble(), gardenData.longitude.toString().toDouble())
            if (::googleMap.isInitialized) moveCameraToCoordinates(markedCoordinate)
        }
        binding.edtKTPRT.setText(gardenData.rt)
        binding.edtKTPRW.setText(gardenData.rw)
        binding.edtKTPAlamat.setText(gardenData.alamat)
        binding.edtKTPKodePos.setText(gardenData.kodePos)
        getProvinsi()
    }

    override fun onResume() {
        super.onResume()
        getProvinsi()
    }

    override fun onError(garden: GardenDataSchema) {
        if (garden.alamat.isNullOrBlank()) {
            binding.edtKTPAlamat.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorKtpAlamat.visibility = View.VISIBLE
        } else {
            binding.edtKTPAlamat.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorKtpAlamat.visibility = View.GONE
        }

        if (garden.rt.isNullOrBlank()) {
            binding.edtKTPRT.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorKtpRt.visibility = View.VISIBLE
        } else {
            binding.edtKTPRT.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorKtpRt.visibility = View.GONE
        }

        if (garden.rw.isNullOrBlank()) {
            binding.edtKTPRW.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorKtpRw.visibility = View.VISIBLE
        } else {
            binding.edtKTPRW.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorKtpRw.visibility = View.GONE
        }

        if (garden.kodePos.isNullOrBlank()) {
            binding.edtKTPKodePos.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorKtpKodePos.visibility = View.VISIBLE
        } else {
            binding.edtKTPKodePos.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorKtpKodePos.visibility = View.GONE
        }
    }
}