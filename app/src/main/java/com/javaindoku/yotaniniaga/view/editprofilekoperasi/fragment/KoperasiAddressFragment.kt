package com.javaindoku.yotaniniaga.view.editprofilekoperasi.fragment

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
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
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
import com.javaindoku.yotaniniaga.databinding.FragmentFarmerAddressKoperasiBinding
import com.javaindoku.yotaniniaga.model.profilePetaniDetail.Data
import com.javaindoku.yotaniniaga.utilities.ConstValue
import com.javaindoku.yotaniniaga.utilities.CustomArrayAdapter
import com.javaindoku.yotaniniaga.utilities.CustomSpinnerAdapter
import com.javaindoku.yotaniniaga.view.editprofilekoperasi.EditProfileKoperasiActivity
import com.javaindoku.yotaniniaga.view.editprofilekoperasi.listener.DataChangedListenerAddress
import com.javaindoku.yotaniniaga.view.editprofilekoperasi.listener.KoperasiDataErrorListener
import kotlinx.android.synthetic.main.fragment_farmer_address.*
import java.util.*

class KoperasiAddressFragment : BaseFragment(), AdapterView.OnItemSelectedListener, OnMapReadyCallback,
    DataChangedListenerAddress, KoperasiDataErrorListener {
    private lateinit var binding: FragmentFarmerAddressKoperasiBinding

    /*@Inject
    lateinit var viewModel: FarmerAddressViewModel*/

    private var permissionRequestCode = 5
    private lateinit var parent: EditProfileKoperasiActivity
    private lateinit var geoCoder: Geocoder
    private lateinit var loadingDialog: Dialog
    private lateinit var searchAdapter: CustomArrayAdapter
    private lateinit var locationProvider: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap
    private lateinit var nextButtonListener: View.OnClickListener
    private lateinit var listProvinsi: MutableList<com.javaindoku.yotaniniaga.model.profile.provinsi.Data>
    private lateinit var listKabupatenKota: MutableList<com.javaindoku.yotaniniaga.model.profile.kabupaten.Data>
    private lateinit var listKecamatan: MutableList<com.javaindoku.yotaniniaga.model.profile.kecamatan.Data>
    private lateinit var listKelurahan: MutableList<com.javaindoku.yotaniniaga.model.profile.kelurahan.Data>
    private lateinit var provinsiAdapter: CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.provinsi.Data>
    private lateinit var kabupatenAdapter: CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.kabupaten.Data>
    private lateinit var kecamatanAdapter: CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.kecamatan.Data>
    private lateinit var kelurahanAdapter: CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.kelurahan.Data>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_farmer_address_koperasi, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parent = activity as EditProfileKoperasiActivity
        searchAdapter = CustomArrayAdapter(requireContext())
        loadingDialog = loadingDialog(requireActivity())
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
            parent.delegateProfile().latitude = address.latitude.toString()
            parent.delegateProfile().longitude = address.longitude.toString()
        })
        binding.btnConfirmationPositive.setOnClickListener(View.OnClickListener {
            parent.submitChanges()
        })
        binding.btnConfirmationNegative.setOnClickListener{
            nextButtonListener.onClick(it)
        }
        binding.edtKTPAlamat.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateProfile().alamat = p0.toString()
            }
        })
        binding.edtKTPRT.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateProfile().rt = p0.toString()
            }
        })
        binding.edtKTPRW.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateProfile().rw = p0.toString()
            }
        })
        binding.edtKTPKodePos.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                parent.delegateProfile().kodePos = p0.toString()
            }
        })
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
        binding.edtKTPAlamat.setText(data.alamat)
        binding.edtKTPRT.setText(data.rt)
        binding.edtKTPRW.setText(data.rw)
        binding.edtKTPKodePos.setText(data.kodePos)
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        var iid = p0!!.getId()
        when (iid) {
            R.id.spinnerProvinsi -> {
                parent.delegateProfile().provinsiId = provinsiAdapter.items[p2].provinsiId.toString()
                parent.getKabupaten(provinsiAdapter.items[p2].provinsiId!!.toInt())
            }

            //Kabupaten Kota
            R.id.spinnerKabupaten -> {
                parent.delegateProfile().kabupatenKotaId = kabupatenAdapter.items[p2].kabupatenKotaId.toString()
                parent.getKecamatan(kabupatenAdapter.items[p2].kabupatenKotaId!!.toInt())
            }

            //Kecamatan Kota
            R.id.spinnerKecamatan -> {
                parent.delegateProfile().kecamatanId = kecamatanAdapter.items[p2].kecamatanId.toString()
                parent.getKelurahan(kecamatanAdapter.items[p2].kecamatanId!!.toInt())
            }

            //Kelurahan Kota
            R.id.spinnerKelurahan -> {
                parent.delegateProfile().kelurahanId = kelurahanAdapter.items[p2].kelurahanDesaId.toString()
                if (parent.delegateProfile().kodePos.isNullOrEmpty()) {
                    parent.delegateProfile().kodePos = kelurahanAdapter.items[p2].kodePos.toString()
                    binding.edtKTPKodePos.setText(kelurahanAdapter.items[p2].kodePos.toString())
                }
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
        }
        moveCameraToCoordinates(LatLng(
            parent.delegateProfile().latitude.toString().toDouble(),
            parent.delegateProfile().longitude.toString().toDouble())
        )
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        nextButtonListener = context as View.OnClickListener
    }

    override fun onDataChanged(profile: Data) {
        bindAddressView(profile)
        initMap()
    }

    override fun onProvinsiListChanged(listProvinsi: List<com.javaindoku.yotaniniaga.model.profile.provinsi.Data>) {
        provinsiAdapter = CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.provinsi.Data>(
            requireContext(),
            listProvinsi
        )
        binding.spinnerProvinsi.adapter = provinsiAdapter
        if (!parent.delegateProfile().provinsiId.toString().isNullOrEmpty()) {
            binding.spinnerProvinsi.setSelection(
                provinsiAdapter.getItemPositionById(parent.delegateProfile().provinsiId.toString())
            )
        }
    }

    override fun onKabupatenListChanged(listKabupaten: List<com.javaindoku.yotaniniaga.model.profile.kabupaten.Data>) {
        kabupatenAdapter = CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.kabupaten.Data>(
            requireContext(),
            listKabupaten
        )
        binding.spinnerKabupaten.adapter = kabupatenAdapter
        if (!parent.delegateProfile().kabupatenKotaId.toString().isNullOrEmpty())
            binding.spinnerKabupaten.setSelection(
                kabupatenAdapter.getItemPositionById(parent.delegateProfile().kabupatenKotaId.toString())
            )
    }

    override fun onKecamatanListChanged(listKecamatan: List<com.javaindoku.yotaniniaga.model.profile.kecamatan.Data>) {
        kecamatanAdapter = CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.kecamatan.Data>(
            requireContext(),
            listKecamatan
        )
        binding.spinnerKecamatan.adapter = kecamatanAdapter
        if (!parent.delegateProfile().kecamatanId.toString().isNullOrEmpty())
            binding.spinnerKecamatan.setSelection(
                kecamatanAdapter.getItemPositionById(parent.delegateProfile().kecamatanId.toString())
            )
    }

    override fun onKelurahanListChanged(listKelurahan: List<com.javaindoku.yotaniniaga.model.profile.kelurahan.Data>) {
        kelurahanAdapter = CustomSpinnerAdapter<com.javaindoku.yotaniniaga.model.profile.kelurahan.Data>(
            requireContext(),
            listKelurahan
        )
        binding.spinnerKelurahan.adapter = kelurahanAdapter
        if (!parent.delegateProfile().kelurahanId.toString().isNullOrEmpty())
            binding.spinnerKelurahan.setSelection(
                kelurahanAdapter.getItemPositionById(parent.delegateProfile().kelurahanId.toString())
            )
    }

    override fun onError(profile: Data) {
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