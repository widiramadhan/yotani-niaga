package com.javaindoku.yotaniniaga.utilities.widget

import android.content.Context
import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.iterator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.javaindoku.yotaniniaga.R
import kotlinx.android.synthetic.main.spinner_with_search_layout.view.*

class SpinnerWithSearch(private val contextt: Context, attributeSet: AttributeSet? = null) :
    LinearLayout(contextt, attributeSet) {

    private val lblSelectedFilter: TextView
    private val textSearch: EditText
    private val rvList: RecyclerView
    private val defList = mutableListOf<String>()
    private var listAdapter: SpinnerWithSearch.SpinnerWSearchAdapter
    private val containerSearch: ConstraintLayout
    private val containerChipGroup: LinearLayout
    private var stateButton: Boolean = false
    private var defaultChoice: String = ""
    private val defaultlbl: TextView
    private var adapterAll: SpinnerAdapter? = null
    private lateinit var onItemSelected: OnItemSelected

    init {
        val root = View.inflate(context, R.layout.spinner_with_search_layout, this)
        textSearch = root.findViewById(R.id.txtSearch)
        lblSelectedFilter = root.findViewById(R.id.lblSelectedFilter)
        rvList = root.findViewById(R.id.listResult)
        containerSearch = root.findViewById(R.id.containerSearch)
        containerChipGroup = root.findViewById(R.id.containerChGroup)
        defaultlbl = root.findViewById(R.id.lblDefault)
        listAdapter = SpinnerWSearchAdapter()
        rvList.adapter = listAdapter
        rvList.layoutManager = LinearLayoutManager(context)
        textSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() == "") {
                    listAdapter.setListItems(defList)
                } else {
                    val newList = mutableListOf<String>()
                    newList.clear()
                    defList.forEach {
                        if (it.contains(s.toString(),true)) {
                            newList.add(it)
                        }
                    }
                    listAdapter.setListItems(newList)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        listAdapter.setItemSelected(object : OnItemClicked {
            override fun onItemSelected(text: String, position: Int) {
                setSelectedItem(text, position)
                containerSearch.visibility = View.GONE
                stateButton = !stateButton
                txtSearch.setText("")
                root.findViewById<ImageView>(R.id.imgArrow)
                    .setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_down))
                val a =
                    contextt.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                a?.hideSoftInputFromWindow(root.windowToken, 0)
            }

        })
        containerChipGroup.setOnClickListener {
            if (!stateButton) {
                containerSearch.visibility = View.VISIBLE
                textSearch.isFocusable = true
                stateButton = !stateButton
                root.findViewById<ImageView>(R.id.imgArrow)
                    .setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_down))
                root.findViewById<ImageView>(R.id.imgArrow).rotation = 180F
            } else {
                containerSearch.visibility = View.GONE
                stateButton = !stateButton
                root.findViewById<ImageView>(R.id.imgArrow)
                    .setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_down))
                txtSearch.setText("")
                val a =
                    contextt.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                a?.hideSoftInputFromWindow(it.windowToken, 0)
            }

        }

        attributeSet?.let {
            val attributes =
                context.obtainStyledAttributes(attributeSet, R.styleable.SpinnerWithSearch)
            setDefaultChoice(attributes.getString(R.styleable.SpinnerWithSearch_defaultChoice)?:"")
            attributes.recycle()
        }



        super.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                containerSearch.visibility = View.GONE
                stateButton = !stateButton
            }
        }
    }

    fun getSelectedItem(onItemSelected: OnItemSelected){
        this.onItemSelected = onItemSelected
    }

    fun setSelectedItem(text: String, position: Int) {
        lblSelectedFilter.text = text
        onItemSelected.onItemSelected(text, position)
    }

    fun setList(items: Collection<String>) {
        if (defaultChoice != "") {
            defList.add(defaultChoice)
        }

        defList.addAll(items)
        listAdapter.setListItems(defList)
    }

    fun setDefaultChoice(text: String) {
        this.defaultChoice = text
        defaultlbl.text = this.defaultChoice
        defaultlbl.visibility = View.VISIBLE
    }

    private  inner class SpinnerWSearchAdapter : RecyclerView.Adapter<SpinnerWSearchAdapter.ViewHolder>() {
        private var listItem = mutableListOf<String>()
        private var listener: OnItemClicked? = null

        fun setListItems(list: List<String>) {
            listItem.clear()
            this.listItem.addAll(list)
            notifyDataSetChanged()
        }

        fun getList(): List<String> {
            return listItem
        }

        fun setItemSelected(l: OnItemClicked) {
            this.listener = l
        }

        inner class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
            val text = itemview.findViewById<TextView>(R.id.text1)
            val container = itemview.findViewById<LinearLayout>(R.id.containerItem)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_spinner_result, parent, false)

            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return listItem.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.text.text = listItem[position]
            holder.container.setBackgroundColor(resources.getColor(R.color.white))
            holder.container.setOnClickListener {
                it.setBackgroundColor(resources.getColor(R.color.md_green_300))
                listener?.onItemSelected(listItem[position], position)
            }
        }
    }

    interface OnItemClicked {
        fun onItemSelected(text: String, position: Int)
    }

    interface OnItemSelected {
        fun onItemSelected(text: String, position: Int)
    }

    fun setAdapter(adapter: SpinnerAdapter){
        this.adapterAll = adapter
    }
}