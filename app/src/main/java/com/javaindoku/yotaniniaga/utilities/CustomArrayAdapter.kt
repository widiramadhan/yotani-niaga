package com.javaindoku.yotaniniaga.utilities

import android.R
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import java.io.IOException


/*
*
*
*@Author
*@Version
*/
//class CustomArrayAdapter(context: Context, var items : List<Address>) : ArrayAdapter<Address>(context, 0, items) {
class CustomArrayAdapter(var context: Context): BaseAdapter(), Filterable {
    private val MAX_RESULTS = 10
    private var resultList: List<Address> = ArrayList()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            view =  (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(android.R.layout.select_dialog_item, parent, false)
        }

        (view?.findViewById(R.id.text1) as TextView).setText(getItem(position).getAddressLine(0))

        return view
    }

    override fun getItem(index: Int): Address {
        return resultList[index];
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return resultList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    val locations: List<Address>? = findLocations(context, constraint.toString())
                    filterResults.values = locations
                    if (locations != null) {
                        filterResults.count = locations.size
                    }
                }
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0 ) {
                    resultList = results.values as List<Address>;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }

        }
    }

    private fun findLocations(context: Context, query_text: String): List<Address>? {
        val result: MutableList<Address> =
            ArrayList<Address>()
        val geocoder = Geocoder(context)
        var addresses: List<Address>? = null
        try {
            // Getting a maximum of 15 Address that matches the input text
            addresses = geocoder.getFromLocationName(query_text, 4)
            for (i in addresses.indices) {
                val address = addresses[i]
                if (address.maxAddressLineIndex != -1) {
                    result.add(address)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }
}
//
//    fun getAllItem(): List<Address> {
//        return items
//    }
//
//    fun refreshItems(newItems: List<Address>) {
//        items = newItems
//        this.clear()
//        this.addAll(newItems)
//        notifyDataSetChanged()
//    }
//
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        return createViewFromResource(position, convertView, parent)
//    }
//
//    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
//        return createViewFromResource(position, convertView, parent)
//    }
//
//    private fun createViewFromResource(position: Int, convertView: View?, parent: ViewGroup?): View{
//        val view: TextView = convertView as TextView? ?: LayoutInflater.from(context)
//            .inflate(android.R.layout.select_dialog_item, parent, false) as TextView
//        if (items[position].maxAddressLineIndex > 0) {
//            view.text = items[position].getAddressLine(0)
//        }
//        return view
//    }
//}
