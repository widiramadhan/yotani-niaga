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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.javaindoku.yotaniniaga.R
import kotlinx.android.synthetic.main.spinner_with_search_layout.view.*

class SpinnerWithSearch2(val contextt: Context, attributeSet: AttributeSet? = null) :
    LinearLayout(contextt, attributeSet) {
//    private val chGroup: ChipGroup
    private val textSearch: EditText
    private val rvList: RecyclerView
    private val defList = mutableListOf<Any>()
    private var listAdapter: SpinnerWithSearch2.SpinnerWSearchAdapter
    private val containerSearch: ConstraintLayout
    private val containerChipGroup: LinearLayout
    private var stateButton: Boolean = false
    private var defaultChoice: Any = ""
    private val defaultlbl: TextView
    private var adapterAll: SpinnerAdapter? = null

    init {
        val root = View.inflate(context, R.layout.spinner_with_search_layout, this)
//        chGroup = root.findViewById(R.id.chipGroupSearch)
        textSearch = root.findViewById(R.id.txtSearch)
        rvList = root.findViewById(R.id.listResult)
        containerSearch = root.findViewById(R.id.containerSearch)
        containerChipGroup = root.findViewById(R.id.containerChGroup)
        defaultlbl = root.findViewById(R.id.lblDefault)
        listAdapter = SpinnerWSearchAdapter()
        rvList.adapter = listAdapter
        rvList.layoutManager = LinearLayoutManager(context)
//        rvList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        textSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() == "") {
                    listAdapter.setListItems(defList)
                } else {
                    val newList = mutableListOf<Any>()
                    newList.clear()
                    defList.forEach {
                        if (it.toString().contains(s.toString(),true)) {
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
            override fun onItemSelected(text: Any) {
               /* var isExist = false
                var isSelectAll = false
                for (chip in chGroup) {
                    if (chip is Chip) {
                        if (chip.text.toString() == text) {
                            isExist = true
                            break
                        }
                    }

                }

                for (chip in chGroup) {
                    if (chip is Chip) {
                        if (chip.text.toString() == defaultChoice) {
                            isSelectAll = true
                            break
                        }
                    }

                }

                if (!isExist&&!isSelectAll) {

                    if(text == defaultChoice){
                        chGroup.removeAllViews()
                    }

                    val chip = Chip(contextt)
                    chip.text = text
                    chip.isCloseIconVisible = true
                    chip.chipStrokeWidth = 1.0f
                    chip.chipStrokeColor =
                        ColorStateList.valueOf(resources.getColor(R.color.md_black_1000))
                    chip.closeIconTint =
                        ColorStateList.valueOf(resources.getColor(R.color.tintcolorcloseiconchip))
                    chip.chipBackgroundColor =
                        ColorStateList.valueOf(resources.getColor(R.color.white))
                    chip.setOnCloseIconClickListener {
                        chGroup.removeView(chip)
                        val a = chGroup.childCount
                        if (a == 0) {
                            defaultlbl.visibility = View.VISIBLE
                        }

                    }

                    chGroup.addView(chip)
                }
                containerSearch.visibility = View.GONE*/
//                addPreLoadChip(text)
                stateButton = !stateButton
//                if (chGroup.childCount > 0) {
//                    defaultlbl.visibility = View.GONE
//                }

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
//                TransitionManager.beginDelayedTransition(containerSearch, AutoTransition())
                containerSearch.visibility = View.VISIBLE
                textSearch.isFocusable = true
                stateButton = !stateButton
                root.findViewById<ImageView>(R.id.imgArrow)
                    .setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_up))
            } else {
//                TransitionManager.beginDelayedTransition(containerSearch, AutoTransition())
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

        /*attributeSet?.let {
            val attributes =
                context.obtainStyledAttributes(attributeSet, R.styleable.SpinnerWithSearch)
            setDefaultChoice(attributes.getString(R.styleable.SpinnerWithSearch_defaultChoice)?:"")
            attributes.recycle()
        }*/



        super.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                containerSearch.visibility = View.GONE
                stateButton = !stateButton
            }
        }
    }

//     fun addPreLoadChip(text: Any) {
//         var isExist = false
//         var isSelectAll = false
//         for (chip in chGroup) {
//             if (chip is Chip) {
//                 if (chip.text.toString() == text.toString()) {
//                     isExist = true
//                     break
//                 }
//             }
//
//         }
//
//         for (chip in chGroup) {
//             if (chip is Chip) {
//                 if (chip.text.toString() == defaultChoice.toString()) {
//                     isSelectAll = true
//                     break
//                 }
//             }
//
//         }
//         if (!isExist&&!isSelectAll) {
//
//             if(text.toString() == defaultChoice.toString()){
//                 chGroup.removeAllViews()
//                 if (chGroup.childCount == 0) {
//                     defaultlbl.visibility = View.VISIBLE
//                 }
//             }
//
//             if(text.toString() != defaultChoice.toString()){
//                 val chip = Chip(contextt)
//                 chip.text = text.toString()
//                 chip.isCloseIconVisible = true
//                 chip.chipStrokeWidth = 1.0f
//                 chip.chipStrokeColor =
//                     ColorStateList.valueOf(resources.getColor(R.color.md_black_1000))
//                 chip.closeIconTint =
//                     ColorStateList.valueOf(resources.getColor(R.color.tintcolorcloseiconchip))
//                 chip.chipBackgroundColor =
//                     ColorStateList.valueOf(resources.getColor(R.color.white))
//                 chip.setOnCloseIconClickListener {
//                     chGroup.removeView(chip)
//                     if (chGroup.childCount == 0) {
//                         defaultlbl.visibility = View.VISIBLE
//                     }
//                 }
//                 chGroup.addView(chip)
//                 defaultlbl.visibility = View.GONE
//             }
//
//
//         }
//         containerSearch.visibility = View.GONE
//    }

    fun setList(items: Collection<Any>) {
        if (defaultChoice != "") {
            defList.add(defaultChoice)
        }

        defList.addAll(items)
        listAdapter.setListItems(defList)
    }

    fun setDefaultChoice(text: Any) {
        this.defaultChoice = text
        defaultlbl.text = this.defaultChoice.toString()
        defaultlbl.visibility = View.VISIBLE
    }

    private  inner class SpinnerWSearchAdapter : RecyclerView.Adapter<SpinnerWSearchAdapter.ViewHolder>() {
        private var listItem = mutableListOf<Any>()
        private var listener: OnItemClicked? = null
        fun setListItems(list: List<Any>) {
            listItem.clear()
            this.listItem.addAll(list)
            notifyDataSetChanged()
        }

        fun getList(): List<Any> {
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
            holder.text.text = listItem[position].toString()
            holder.container.setBackgroundColor(resources.getColor(R.color.white))
            holder.container.setOnClickListener {
                it.setBackgroundColor(resources.getColor(R.color.md_green_300))
                listener?.onItemSelected(listItem[position].toString())
            }
        }
    }

    interface OnItemClicked {
        fun onItemSelected(text: Any)
    }

    fun setAdapter(adapter: SpinnerAdapter){
        this.adapterAll = adapter
    }

//    fun getSelectedItem(): List<Any> {
//        var returnResult = mutableListOf<Any>()
//        if (defaultlbl.visibility == View.VISIBLE) {
//            returnResult.add(defaultChoice)
//        } else {
//            for (chip in chGroup) {
//                if (chip is Chip) {
//                    defList.forEach {
//                        if(it.toString() == chip.text.toString()){
//                            returnResult.add(it)
//                        }
//                    }
//
//                }
//            }
//        }
//        return returnResult
//    }
//
//    fun clearSelectionItem(){
//        chGroup.removeAllViews()
//        defaultlbl.visibility = View.VISIBLE
//    }
}