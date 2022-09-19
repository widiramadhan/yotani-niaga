package com.javaindoku.yotaniniaga.utilities

import android.content.Context
import android.location.Address
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.javaindoku.yotaniniaga.R
import kotlinx.android.synthetic.main.item_spinner_result.view.*

/*
*
*
*@Author
*@Version
*/
class CustomSpinnerAdapter<T: AdapterDisplayText>(var mContext: Context, var items: List<T>) :
    ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, items.map{it.displayName()}) {

    init {
        var listOfDisplay = items
    }

    fun getItemPositionById(id: Any): Int {
        items.forEachIndexed { index, element ->
            if (element.selectId() == id) return index
        }
        return -1;
    }

    fun getItemData(position: Int) : T {
        return items[position]
    }

    override fun getItem(index: Int): String {
        return items[index].displayName();
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getPosition(item: String?): Int {
        return super.getPosition(item)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    private fun createViewFromResource(position: Int, convertView: View?, parent: ViewGroup?): View{
        var view = convertView
        if (view == null) {
            view =  (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(android.R.layout.select_dialog_item, parent, false)
        }
        (view?.findViewById(android.R.id.text1) as TextView).setText(items[position].displayName())
        return view
    }
}

interface AdapterDisplayText {
    fun displayName(): String
    fun selectId(): Any
}
