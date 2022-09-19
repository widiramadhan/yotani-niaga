package com.javaindoku.yotaniniaga.view.listgarden.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.javaindoku.yotaniniaga.databinding.ItemGardenBinding
import com.javaindoku.yotaniniaga.model.gardenPetani.GardenDataSchema
import com.javaindoku.yotaniniaga.utilities.ConstValue
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener


class GardenAdapter (
    private val context: Context,
    private val gardenList: List<GardenDataSchema>,
    private val onClickPks: OnClickGarden,
    private val childFragmentManager: FragmentManager,
) : RecyclerView.Adapter<GardenAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemGardenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(context, binding, onClickPks, childFragmentManager)
    }

    class ItemViewHolder(
        private val context: Context,
        private val binding: ItemGardenBinding,
        private val onClickPks: OnClickGarden,
        private val childFragmentManager: FragmentManager
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(item: GardenDataSchema) {
            binding.lblGardenArea.text = item.luasKebun
            binding.lblLocation.text = "${item.alamat}, ${item.kelurahanName}, ${item.kecamatanName}, " +
                                        "${item.kabupatenKotaName}, ${item.provinsiName}, ${item.kodePos}"
            binding.lblTotalTree.text = item.potensiProduksi
            binding.lblYearsOfPlant.text = item.tahunTanamId
            binding.lytEditGarden.setOnSafeClickListener {
                onClickPks.toEditGarden(item)
            }
            bindMap(
                binding.mapLayout,
                if (!item.id.isNullOrEmpty()) item.id.toString().toInt() else item.kebunId.toString().toInt(),
                LatLng(item.latitude.toString().toDouble(), item.longitude.toString().toDouble())
            )
        }

        private fun bindMap(view: FrameLayout, id: Int, coordinates: LatLng) {
            val map = FrameLayout(context)
            map.id = id
            val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            map.layoutParams = layoutParams
            view.addView(map)
            val options = GoogleMapOptions()
            options.liteMode(true)
            val mapFragment = SupportMapFragment.newInstance(options)
            mapFragment.getMapAsync(OnMapReadyCallback { googleMap ->
                var myLocation = CameraUpdateFactory.newLatLngZoom(coordinates, ConstValue.MAP_ZOOM_VALUE)
                googleMap.moveCamera(myLocation)
            })
            childFragmentManager.beginTransaction().add(map.id, mapFragment).commit()
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item: GardenDataSchema = gardenList[position]
        holder.bindItem(item)
    }

    override fun getItemCount(): Int {
        return gardenList.size
    }

    interface OnClickGarden {
        fun toEditGarden(pks: GardenDataSchema)
    }
}