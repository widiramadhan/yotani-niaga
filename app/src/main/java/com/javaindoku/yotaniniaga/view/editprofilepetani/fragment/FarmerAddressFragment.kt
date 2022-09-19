package com.javaindoku.yotaniniaga.view.editprofilepetani.fragment

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.opengl.Visibility
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
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
import com.javaindoku.yotaniniaga.databinding.FragmentFarmerAddressBinding
import com.javaindoku.yotaniniaga.model.profilePetaniDetail.Data
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.utilities.ConstValue
import com.javaindoku.yotaniniaga.utilities.CustomArrayAdapter
import com.javaindoku.yotaniniaga.utilities.CustomSpinnerAdapter
import com.javaindoku.yotaniniaga.view.editprofilepetani.EditProfilePetaniActivity
import com.javaindoku.yotaniniaga.view.editprofilepetani.listener.DataChangedListenerAddress
import com.javaindoku.yotaniniaga.view.editprofilepetani.listener.DataChangedListenerProfile
import com.javaindoku.yotaniniaga.view.editprofilepetani.listener.FarmerDataErrorListener
import com.javaindoku.yotaniniaga.viewmodel.FarmerAddressViewModel
import kotlinx.android.synthetic.main.fragment_farmer_address.*
import java.util.*
import javax.inject.Inject

class FarmerAddressFragment : BaseFragment(), AdapterView.OnItemSelectedListener, OnMapReadyCallback,
    DataChangedListenerAddress, FarmerDataErrorListener {
    private lateinit var binding: FragmentFarmerAddressBinding

    @Inject
    lateinit var viewModel: FarmerAddressViewModel

    private var permissionRequestCode = 5
    private lateinit var parent: EditProfilePetaniActivity
    private lateinit var geoCoder: Geocoder
    private lateinit var loadingDialog: Dialog
    private lateinit var searchAdapter: CustomArrayAdapter
    private lateinit var searchAdapter2: CustomArrayAdapter
    private lateinit var locationProvider: FusedLocationProviderClient
    private lateinit var locationProvider2: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap
    private lateinit var googleMap2: GoogleMap
    private lateinit var nextButtonListener: View.OnClickListener
    private lateinit var listProvinsi: MutableList<com.javaindoku.yotaniniaga.model.profile.provinsi.Data>
    private lateinit var listKabupatenKota: MutableList<com.javaindoku.yotaniniaga.model.profile.kabupaten.Data>
    private lateinit var listKecamatan: MutableList<com.javaindoku.yotaniniaga.model.profile.kecamatan.Data>
    private lateinit var listKelurahan: MutableList<com.javaindoku.yotaniniaga.model.profile.kelurahan.Data>
    private lateinit var provinsiAdapter: CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.provinsi.Data>
    private lateinit var kabupatenAdapter: CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.kabupaten.Data>
    private lateinit var kecamatanAdapter: CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.kecamatan.Data>
    private lateinit var kelurahanAdapter: CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.kelurahan.Data>
    private lateinit var listProvinsiDomisili: MutableList<com.javaindoku.yotaniniaga.model.profile.provinsi.Data>
    private lateinit var listKabupatenKotaDomisili: MutableList<com.javaindoku.yotaniniaga.model.profile.kabupaten.Data>
    private lateinit var listKecamatanDomisili: MutableList<com.javaindoku.yotaniniaga.model.profile.kecamatan.Data>
    private lateinit var listKelurahanDomisili: MutableList<com.javaindoku.yotaniniaga.model.profile.kelurahan.Data>
    private lateinit var provinsiAdapterDomisili: CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.provinsi.Data>
    private lateinit var kabupatenAdapterDomisili: CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.kabupaten.Data>
    private lateinit var kecamatanAdapterDomisili: CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.kecamatan.Data>
    private lateinit var kelurahanAdapterDomisili: CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.kelurahan.Data>

    private var ceklisKTP = "Y"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_farmer_address, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parent = activity as EditProfilePetaniActivity
        parent.setAddressDataChangeNotifier(this)
        searchAdapter = CustomArrayAdapter(requireContext())
        searchAdapter2 = CustomArrayAdapter(requireContext())
        loadingDialog = loadingDialog(requireActivity())

        // PILIH ALAMAT KTP
        binding.spinnerProvinsi.onItemSelectedListener = this
        binding.spinnerProvinsi.setTitle(getString(R.string.srSelectProvince))
        binding.spinnerKabupaten.onItemSelectedListener = this
        binding.spinnerKabupaten.setTitle(getString(R.string.srSelectCity))
        binding.spinnerKecamatan.onItemSelectedListener = this
        binding.spinnerKabupaten.setTitle(getString(R.string.srSelectDistrict))
        binding.spinnerKelurahan.onItemSelectedListener = this
        binding.spinnerKelurahan.setTitle(getString(R.string.srSelectSubDistrict))

        // PILIH ALAMAT DOMISI
        binding.spinnerProvinsiDomisili.onItemSelectedListener = this
        binding.spinnerProvinsiDomisili.setTitle(getString(R.string.srSelectProvince))
        binding.spinnerKabupatenDomisli.onItemSelectedListener = this
        binding.spinnerKabupatenDomisli.setTitle(getString(R.string.srSelectCity))
        binding.spinnerKecamatanDomisili.onItemSelectedListener = this
        binding.spinnerKabupatenDomisli.setTitle(getString(R.string.srSelectDistrict))
        binding.spinnerKelurahanDomisili.onItemSelectedListener = this
        binding.spinnerKelurahanDomisili.setTitle(getString(R.string.srSelectSubDistrict))

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

        binding.spinnerProvinsiDomisili.setOnSearchTextChangedListener {
            if (binding.spinnerProvinsiDomisili != null) {
                for (i in 0..listProvinsiDomisili.size - 1) {
                    if(listProvinsiDomisili.get(i).provinsiName.equals(binding.spinnerProvinsiDomisili.selectedItem.toString())){
                        //setBackPagetrue()
                        break
                    }

                    if(i == (listProvinsiDomisili.size-1)){
                        if(!listProvinsiDomisili.get(i).provinsiName.equals(binding.spinnerProvinsiDomisili.selectedItem.toString())){
                            //setBackPageFalse()
                        }
                    }
                }
            }else{
                //setBackPageFalse()
            }
        }
        binding.spinnerKabupatenDomisli.setOnSearchTextChangedListener {
            if (binding.spinnerKabupatenDomisli != null) {
                for (i in 0..listKabupatenKotaDomisili.size - 1) {
                    if(listKabupatenKotaDomisili.get(i).kabupatenKotaName.equals(binding.spinnerKabupatenDomisli.selectedItem.toString())){
                        //setBackPagetrue()
                        break
                    }

                    if(i == (listKabupatenKotaDomisili.size-1)){
                        if(!listKabupatenKotaDomisili.get(i).kabupatenKotaName.equals(binding.spinnerKabupatenDomisli.selectedItem.toString())){
                            //setBackPageFalse()
                        }
                    }
                }
            }else{
                //setBackPageFalse()
            }
        }
        binding.spinnerKecamatanDomisili.setOnSearchTextChangedListener {
            if (binding.spinnerKecamatanDomisili != null) {
                for (i in 0..listKecamatanDomisili.size - 1) {
                    if(listKecamatanDomisili.get(i).kecamatanName.equals(binding.spinnerKecamatanDomisili.selectedItem.toString())){
                        //setBackPagetrue()
                        break
                    }

                    if(i == (listKecamatanDomisili.size-1)){
                        if(!listKecamatanDomisili.get(i).kecamatanName.equals(binding.spinnerKecamatanDomisili.selectedItem.toString())){
                            //setBackPageFalse()
                        }
                    }
                }
            }else{
                //setBackPageFalse()
            }
        }
        binding.spinnerKelurahanDomisili.setOnSearchTextChangedListener {
            if (binding.spinnerKelurahanDomisili != null) {
                for (i in 0..listKelurahanDomisili.size - 1) {
                    if(listKelurahanDomisili.get(i).kelurahanDesaName.equals(binding.spinnerKelurahanDomisili.selectedItem.toString())){
                        //setBackPagetrue()
                        break
                    }

                    if(i == (listKelurahanDomisili.size-1)){
                        if(!listKelurahanDomisili.get(i).kelurahanDesaName.equals(binding.spinnerKelurahanDomisili.selectedItem.toString())){
                            //setBackPageFalse()
                        }
                    }
                }
            }else{
                //setBackPageFalse()
            }
        }

        // MAPS KTP
        binding.edtxMapSearch.setAdapter(searchAdapter)
        binding.edtxMapSearch.setOnItemClickListener(AdapterView.OnItemClickListener { adapterView, view, position, id ->
            var address = adapterView.getItemAtPosition(position) as Address
            edtxMapSearch.setText(address.getAddressLine(0))
            moveCameraToCoordinates(LatLng(address.latitude, address.longitude))
            parent.delegateProfile().latitude = address.latitude.toString()
            parent.delegateProfile().longitude = address.longitude.toString()
        })

        // MAPS  DOMISILI
        binding.edtxMapSearch2.setAdapter(searchAdapter2)
        binding.edtxMapSearch2.setOnItemClickListener(AdapterView.OnItemClickListener { adapterView, view, position, id ->
            var address2 = adapterView.getItemAtPosition(position) as Address
            edtxMapSearch2.setText(address2.getAddressLine(0))
            moveCameraToCoordinatesDomisili(LatLng(address2.latitude, address2.longitude))
            /*parent.delegateProfile().latitudeDomisili = address2.latitude.toString()
            parent.delegateProfile().longitudeDomisili = address2.longitude.toString()*/
        })

        binding.btnConfirmationPositive.setOnClickListener(View.OnClickListener {
            if(parent.delegateProfile().ceklisKtp == "Y"){
                parent.delegateProfile().alamat = parent.delegateProfile().ktpAlamat
                parent.delegateProfile().rt = parent.delegateProfile().ktpRT
                parent.delegateProfile().rw = parent.delegateProfile().ktpRW
                parent.delegateProfile().provinsiId = parent.delegateProfile().ktpProvinsiId
                parent.delegateProfile().kabupatenKotaId = parent.delegateProfile().ktpKabupatenKotaId
                parent.delegateProfile().kecamatanId = parent.delegateProfile().ktpKecamatanId
                parent.delegateProfile().kelurahanId = parent.delegateProfile().ktpKelurahanId
                parent.delegateProfile().kodePos = parent.delegateProfile().ktpKodePos
            }
            parent.submitChanges()
        })
        binding.btnConfirmationNegative.setOnClickListener{
            nextButtonListener.onClick(it)
        }
        binding.edtKTPAlamat.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateProfile().ktpAlamat = p0.toString()
            }
        })
        binding.edtKTPRT.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateProfile().ktpRT = p0.toString()
            }
        })
        binding.edtKTPRW.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateProfile().ktpRW = p0.toString()
            }
        })
        binding.edtKTPKodePos.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateProfile().ktpKodePos = p0.toString()
            }
        })
        binding.edtDomisiliAlamat.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateProfile().alamat = p0.toString()
            }
        })
        binding.edtDomisiliRT.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateProfile().rt = p0.toString()
            }
        })
        binding.edtDomisiliRW.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateProfile().rw = p0.toString()
            }
        })
        binding.edtDomisliKodePos.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateProfile().kodePos = p0.toString()
            }
        })

        binding.cekSesuaiKTP.setOnClickListener {
            if (cekSesuaiKTP.isChecked) {
                ceklisKTP == "Y"
                binding.lytDomisili.visibility = View.GONE
                parent.delegateProfile().ceklisKtp = "Y"
            } else {
                ceklisKTP == "N"
                binding.lytDomisili.visibility = View.VISIBLE
                parent.delegateProfile().ceklisKtp = "N"
            }
        }
    }

    private fun initMap() {
        locationProvider = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (!checkLocationPermission()) return;
        val mapFragment = childFragmentManager.findFragmentById(R.id.map)
                as SupportMapFragment
        locationProvider.lastLocation.addOnSuccessListener {
            if (parent.delegateProfile().longitude.toString().isNullOrEmpty() || parent.delegateProfile().latitude.toString().isNullOrEmpty()) {
                parent.delegateProfile().longitude = it.longitude.toString()
                parent.delegateProfile().latitude = it.latitude.toString()
            }
            mapFragment.getMapAsync(this)
        }
    }

    private fun initMap2() {
        locationProvider2 = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (!checkLocationPermission()) return;
        val mapFragment = childFragmentManager.findFragmentById(R.id.map2)
                as SupportMapFragment
        locationProvider2.lastLocation.addOnSuccessListener {
            /*if (parent.delegateProfile().longitudeDomisili.toString().isNullOrEmpty() || parent.delegateProfile().latitudeDomisili.toString().isNullOrEmpty()) {
                parent.delegateProfile().longitudeDomisili = it.longitude.toString()
                parent.delegateProfile().latitudeDomisili = it.latitude.toString()
            }*/
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

    private fun bindAddressView(data: Data) {
        binding.edtKTPAlamat.setText(data.ktpAlamat)
        binding.edtKTPRT.setText(data.ktpRT)
        binding.edtKTPRW.setText(data.ktpRW)
        binding.edtKTPKodePos.setText(data.ktpKodePos)
        binding.edtDomisiliAlamat.setText(data.alamat)
        binding.edtDomisiliRT.setText(data.rt)
        binding.edtDomisiliRW.setText(data.rw)
        binding.edtDomisliKodePos.setText(data.kodePos)

        if(data.kelurahanId == data.ktpKelurahanId){
            cekSesuaiKTP.isChecked = true
            ceklisKTP == "Y"
            binding.lytDomisili.visibility = View.GONE
            parent.delegateProfile().ceklisKtp = "Y"
        }else{
            cekSesuaiKTP.isChecked = false
            ceklisKTP == "N"
            binding.lytDomisili.visibility = View.VISIBLE
            parent.delegateProfile().ceklisKtp = "N"
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        var iid = p0!!.getId()
        when (iid) {

            //provinsi KTP
            R.id.spinnerProvinsi -> {
                parent.delegateProfile().ktpProvinsiId = provinsiAdapter.items[p2].provinsiId.toString()
                parent.getKabupaten(provinsiAdapter.items[p2].provinsiId!!.toInt())
            }

            //Kabupaten Kota KTP
            R.id.spinnerKabupaten -> {
                parent.delegateProfile().ktpKabupatenKotaId = kabupatenAdapter.items[p2].kabupatenKotaId.toString()
                parent.getKecamatan(kabupatenAdapter.items[p2].kabupatenKotaId!!.toInt())
            }

            //Kecamatan Kota KTP
            R.id.spinnerKecamatan -> {
                parent.delegateProfile().ktpKecamatanId = kecamatanAdapter.items[p2].kecamatanId.toString()
                parent.getKelurahan(kecamatanAdapter.items[p2].kecamatanId!!.toInt())
            }

            //Kelurahan Kota KTP
            R.id.spinnerKelurahan -> {
                parent.delegateProfile().ktpKelurahanId = kelurahanAdapter.items[p2].kelurahanDesaId.toString()
                parent.delegateProfile().ktpKodePos = kelurahanAdapter.items[p2].kodePos.toString()
                binding.edtKTPKodePos.setText(kelurahanAdapter.items[p2].kodePos.toString())
                /*if (parent.delegateProfile().kodePos.isNullOrEmpty()) {
                    parent.delegateProfile().kodePos = kelurahanAdapter.items[p2].kodePos.toString()
                    binding.edtKTPKodePos.setText(kelurahanAdapter.items[p2].kodePos.toString())
                }*/
            }

            // Provinsi Domisili
            R.id.spinnerProvinsiDomisili -> {
                parent.delegateProfile().provinsiId = provinsiAdapterDomisili.items[p2].provinsiId.toString()
                parent.getKabupatenDomisili(provinsiAdapterDomisili.items[p2].provinsiId!!.toInt())
            }

            //Kabupaten Kota Domisili
            R.id.spinnerKabupatenDomisli -> {
                parent.delegateProfile().kabupatenKotaId = kabupatenAdapterDomisili.items[p2].kabupatenKotaId.toString()
                parent.getKecamatanDomisili(kabupatenAdapterDomisili.items[p2].kabupatenKotaId!!.toInt())
            }

            //Kecamatan Kota Domisili
            R.id.spinnerKecamatanDomisili -> {
                parent.delegateProfile().kecamatanId = kecamatanAdapterDomisili.items[p2].kecamatanId.toString()
                parent.getKelurahanDomisili(kecamatanAdapterDomisili.items[p2].kecamatanId!!.toInt())
            }

            //Kelurahan Kota Domisili
            R.id.spinnerKelurahanDomisili -> {
                parent.delegateProfile().kelurahanId = kelurahanAdapterDomisili.items[p2].kelurahanDesaId.toString()
                parent.delegateProfile().kodePos = kelurahanAdapterDomisili.items[p2].kodePos.toString()
                binding.edtDomisliKodePos.setText(kelurahanAdapterDomisili.items[p2].kodePos.toString())
                /*if (parent.delegateProfile().kodePosDomisili.isNullOrEmpty()) {
                    parent.delegateProfile().kodePosDomisili = kelurahanAdapterDomisili.items[p2].kodePos.toString()
                    binding.edtDomisliKodePos.setText(kelurahanAdapterDomisili.items[p2].kodePos.toString())
                }*/
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        geoCoder = Geocoder(requireActivity(), Locale.getDefault())
        if (googleMap != null) {
            this.googleMap = googleMap
            this.googleMap2 = googleMap
        }
        // KTP
        moveCameraToCoordinates(LatLng(
            parent.delegateProfile().latitude.toString().toDouble(),
            parent.delegateProfile().longitude.toString().toDouble())
        )
        // Domisili
        /*moveCameraToCoordinatesDomisili(LatLng(
            parent.delegateProfile().latitudeDomisili.toString().toDouble(),
            parent.delegateProfile().longitudeDomisili.toString().toDouble())
        )*/
    }

    private fun moveCameraToCoordinates(coordinates: LatLng) {
        var myLocation = CameraUpdateFactory.newLatLngZoom(coordinates, ConstValue.MAP_ZOOM_VALUE)
        this.googleMap.animateCamera(myLocation)
    }

    private fun moveCameraToCoordinatesDomisili(coordinates: LatLng) {
        var myLocation = CameraUpdateFactory.newLatLngZoom(coordinates, ConstValue.MAP_ZOOM_VALUE)
        this.googleMap2.animateCamera(myLocation)
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
                initMap2()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        nextButtonListener = context as View.OnClickListener
    }

    override fun onDataChanged(profile: Data) {
        bindAddressView(profile)
        initMap()
        initMap2()
    }

    override fun onProvinsiListChanged(dataProvinsi: List<com.javaindoku.yotaniniaga.model.profile.provinsi.Data>) {
        listProvinsi = dataProvinsi.toMutableList()
        provinsiAdapter = CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.provinsi.Data>(
            requireContext(),
            dataProvinsi
        )
        binding.spinnerProvinsi.adapter = provinsiAdapter
        if (!parent.delegateProfile().ktpProvinsiId.toString().isNullOrEmpty()) {
            binding.spinnerProvinsi.setSelection(
                provinsiAdapter.getItemPositionById(parent.delegateProfile().ktpProvinsiId.toString())
            )
        }
    }

    override fun onKabupatenListChanged(dataKabupaten: List<com.javaindoku.yotaniniaga.model.profile.kabupaten.Data>) {
        listKabupatenKota = dataKabupaten.toMutableList()
        kabupatenAdapter = CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.kabupaten.Data>(
            requireContext(),
            dataKabupaten
        )
        binding.spinnerKabupaten.adapter = kabupatenAdapter
        if (!parent.delegateProfile().ktpKabupatenKotaId.toString().isNullOrEmpty())
            binding.spinnerKabupaten.setSelection(
                kabupatenAdapter.getItemPositionById(parent.delegateProfile().ktpKabupatenKotaId.toString())
            )
    }

    override fun onKecamatanListChanged(dataKecamatan: List<com.javaindoku.yotaniniaga.model.profile.kecamatan.Data>) {
        listKecamatan = dataKecamatan.toMutableList()
        kecamatanAdapter = CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.kecamatan.Data>(
            requireContext(),
            dataKecamatan
        )
        binding.spinnerKecamatan.adapter = kecamatanAdapter
        if (!parent.delegateProfile().ktpKecamatanId.toString().isNullOrEmpty())
            binding.spinnerKecamatan.setSelection(
                kecamatanAdapter.getItemPositionById(parent.delegateProfile().ktpKecamatanId.toString())
            )
    }

    override fun onKelurahanListChanged(dataKelurahan: List<com.javaindoku.yotaniniaga.model.profile.kelurahan.Data>) {
        listKelurahan = dataKelurahan.toMutableList()
        kelurahanAdapter = CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.kelurahan.Data>(
            requireContext(),
            dataKelurahan
        )
        binding.spinnerKelurahan.adapter = kelurahanAdapter
        if (!parent.delegateProfile().ktpKelurahanId.toString().isNullOrEmpty())
            binding.spinnerKelurahan.setSelection(
                kelurahanAdapter.getItemPositionById(parent.delegateProfile().ktpKelurahanId.toString())
            )
    }

    override fun onProvinsiDomisiliListChanged(dataProvinsi: List<com.javaindoku.yotaniniaga.model.profile.provinsi.Data>) {
        listProvinsiDomisili = dataProvinsi.toMutableList()
        provinsiAdapterDomisili = CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.provinsi.Data>(
            requireContext(),
            dataProvinsi
        )
        binding.spinnerProvinsiDomisili.adapter = provinsiAdapterDomisili
        if (!parent.delegateProfile().provinsiId.toString().isNullOrEmpty()) {
            binding.spinnerProvinsiDomisili.setSelection(
                provinsiAdapterDomisili.getItemPositionById(parent.delegateProfile().provinsiId.toString())
            )
        }
    }

    override fun onKabupatenDomisiliListChanged(dataKabupaten: List<com.javaindoku.yotaniniaga.model.profile.kabupaten.Data>) {
        listKabupatenKotaDomisili = dataKabupaten.toMutableList()
        kabupatenAdapterDomisili = CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.kabupaten.Data>(
            requireContext(),
            dataKabupaten
        )
        binding.spinnerKabupatenDomisli.adapter = kabupatenAdapterDomisili
        if (!parent.delegateProfile().kabupatenKotaId.toString().isNullOrEmpty())
            binding.spinnerKabupatenDomisli.setSelection(
                kabupatenAdapterDomisili.getItemPositionById(parent.delegateProfile().kabupatenKotaId.toString())
            )
    }

    override fun onKecamatanDomisiliListChanged(dataKecamatan: List<com.javaindoku.yotaniniaga.model.profile.kecamatan.Data>) {
        listKecamatanDomisili = dataKecamatan.toMutableList()
        kecamatanAdapterDomisili = CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.kecamatan.Data>(
            requireContext(),
            dataKecamatan
        )
        binding.spinnerKecamatanDomisili.adapter = kecamatanAdapterDomisili
        if (!parent.delegateProfile().kecamatanId.toString().isNullOrEmpty())
            binding.spinnerKecamatanDomisili.setSelection(
                kecamatanAdapterDomisili.getItemPositionById(parent.delegateProfile().kecamatanId.toString())
            )
    }

    override fun onKelurahanDomisiliListChanged(dataKelurahan: List<com.javaindoku.yotaniniaga.model.profile.kelurahan.Data>) {
        listKelurahanDomisili = dataKelurahan.toMutableList()
        kelurahanAdapterDomisili = CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.kelurahan.Data>(
            requireContext(),
            dataKelurahan
        )
        binding.spinnerKelurahanDomisili.adapter = kelurahanAdapterDomisili
        if (!parent.delegateProfile().kelurahanId.toString().isNullOrEmpty())
            binding.spinnerKelurahanDomisili.setSelection(
                kelurahanAdapterDomisili.getItemPositionById(parent.delegateProfile().kelurahanId.toString())
            )
    }

    override fun onError(profile: Data) {
        if (profile.ktpAlamat.isNullOrBlank()) {
            binding.edtKTPAlamat.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorKtpAlamat.visibility = View.VISIBLE
        } else {
            binding.edtKTPAlamat.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorKtpAlamat.visibility = View.GONE
        }

        if (profile.ktpRT.isNullOrBlank()) {
            binding.edtKTPRT.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorKtpRt.visibility = View.VISIBLE
        } else {
            binding.edtKTPRT.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorKtpRt.visibility = View.GONE
        }

        if (profile.ktpRW.isNullOrBlank()) {
            binding.edtKTPRW.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorKtpRw.visibility = View.VISIBLE
        } else {
            binding.edtKTPRW.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorKtpRw.visibility = View.GONE
        }

        if (profile.ktpKodePos.isNullOrBlank()) {
            binding.edtKTPKodePos.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorKtpKodePos.visibility = View.VISIBLE
        } else {
            binding.edtKTPKodePos.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorKtpKodePos.visibility = View.GONE
        }

        if (profile.alamat.isNullOrBlank()) {
            binding.edtDomisiliAlamat.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorDomisiliAlamat.visibility = View.VISIBLE
        } else {
            binding.edtDomisiliAlamat.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorDomisiliAlamat.visibility = View.GONE
        }

        if (profile.rt.isNullOrBlank()) {
            binding.edtDomisiliRT.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorDomisiRt.visibility = View.VISIBLE
        } else {
            binding.edtDomisiliRT.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorDomisiRt.visibility = View.GONE
        }

        if (profile.rw.isNullOrBlank()) {
            binding.edtDomisiliRW.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorDomisiliRw.visibility = View.VISIBLE
        } else {
            binding.edtDomisiliRW.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorDomisiliRw.visibility = View.GONE
        }

        if (profile.kodePos.isNullOrBlank()) {
            binding.edtDomisliKodePos.setBackgroundResource(R.drawable.shape_text_input_error)
            binding.lblErrorDomisiliKodePos.visibility = View.VISIBLE
        } else {
            binding.edtDomisliKodePos.setBackgroundResource(R.drawable.shape_text_input)
            binding.lblErrorDomisiliKodePos.visibility = View.GONE
        }
    }

}