package com.javaindoku.yotaniniaga.view.schedulefertilization

import android.content.Context
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.threetenabp.AndroidThreeTen
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseActivity
import com.javaindoku.yotaniniaga.databinding.ActivityScheduleFertilizationBinding
import com.javaindoku.yotaniniaga.databinding.Example4CalendarDayBinding
import com.javaindoku.yotaniniaga.databinding.Example4CalendarHeaderBinding
import com.javaindoku.yotaniniaga.model.schedulefertilization.ScheduleFertilization
import com.javaindoku.yotaniniaga.utilities.SharedPrefKeys
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import dagger.android.AndroidInjection
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
import org.threeten.bp.temporal.WeekFields
import java.util.*

class ScheduleFertilizationActivity : BaseActivity() {
    private lateinit var binding: ActivityScheduleFertilizationBinding

    private val today = LocalDate.now()

    private var listScheduleFertiliztion = mutableListOf<ScheduleFertilization>()

    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMM")

    private val startBackground: GradientDrawable by lazy {
        applicationContext.getDrawableCompat(R.drawable.example_4_continuous_selected_bg_start) as GradientDrawable
    }

    private val endBackground: GradientDrawable by lazy {
        applicationContext.getDrawableCompat(R.drawable.example_4_continuous_selected_bg_end) as GradientDrawable
    }

    private var selectedDate : LocalDate ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_schedule_fertilization)
        binding.appBarLayout.lblTitleToolbarHome.text = getString(R.string.scheduleFertilizationTitle)
        binding.appBarLayout.imgBackToolbarHome.setOnSafeClickListener {
            finish()
        }

        binding.clvScheduleFertilization.post {
            val radius = ((binding.clvScheduleFertilization.width / 7) / 2).toFloat()
            startBackground.setCornerRadius(topLeft = radius, bottomLeft = radius)
            endBackground.setCornerRadius(topRight = radius, bottomRight = radius)
        }

        val currentMonth = YearMonth.now()
        val firstDayOfWeek = DayOfWeek.MONDAY
        binding.clvScheduleFertilization.setup(currentMonth, currentMonth.plusMonths(12), firstDayOfWeek)
        binding.clvScheduleFertilization.scrollToMonth(currentMonth)

        // Set the First day of week depending on Locale
        val daysOfWeek = daysOfWeekFromLocale()
        binding.legendLayout.legendLayout.children.forEachIndexed { index, view ->
            (view as TextView).apply {

                var lang = prefHelper.getStringFromShared(SharedPrefKeys.LANGUAGE)
                lang = when (lang) {
                    "en" -> "en"
                    else -> "in"
                }
                when (lang) {
                    "en" -> text = daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                    else -> text = daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale("in"))
                }

//                text = daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale("in"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                setTextColorRes(R.color.color_title_home)
            }
        }

        val startDate = LocalDate.of(2020, 9, 9)
        val endDate = LocalDate.of(2020, 9, 10)
        val title1 = "Pemupukan di kebun 1"
        listScheduleFertiliztion.add(ScheduleFertilization(startDate!!, endDate!!, title1))

        val startDate2 = LocalDate.of(2020, 9, 12)
        val endDate2 = LocalDate.of(2020, 9, 14)
        val title2 = "Pemupukan di kebun 2"
        listScheduleFertiliztion.add(ScheduleFertilization(startDate2!!, endDate2!!, title2))

        setSelectedColorSchedule()

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val textView = Example4CalendarHeaderBinding.bind(view).exFourHeaderText
        }
        binding.clvScheduleFertilization.monthHeaderBinder = object :
            MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                val monthTitle = "${month.yearMonth.month.name.toLowerCase().capitalize()} ${month.year}"
                container.textView.text = monthTitle
                container.textView.visibility = View.GONE
            }
        }

        binding.clvScheduleFertilization.monthScrollListener = {
            val yearMonthSelected = it.yearMonth
            val monthTitle = "${monthTitleFormatter.format(it.yearMonth)} ${it.year}"
            binding.lblMonthYearDate.text = monthTitle
            binding.imgNextMonth.setOnSafeClickListener {
                binding.clvScheduleFertilization.scrollToMonth(yearMonthSelected.plusMonths(1))
            }

            binding.imgPreviousMonth.setOnSafeClickListener {
                binding.clvScheduleFertilization.scrollToMonth(yearMonthSelected.minusMonths(1))
            }
        }

        binding.imgNextMonth.setOnSafeClickListener {
            val yearMonth = binding.clvScheduleFertilization.findLastVisibleMonth()

//            binding.clvScheduleFertilization.scrollToMonth(selectedMonthAndYear)
        }
    }

    private fun setSelectedColorSchedule() {
        binding.clvScheduleFertilization.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) : DayViewContainer {
                val dayViewContainer = DayViewContainer(view, listScheduleFertiliztion, binding.clvScheduleFertilization)
                if(dayViewContainer.selectedDate != null)
                    selectedDate = dayViewContainer.selectedDate!!
                return dayViewContainer
            }
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                selectedDate = container.selectedDate
                val textView = container.binding.exFourDayText
                val roundBgView = container.binding.exFourRoundBgView

                textView.text = null
                textView.background = null
                roundBgView.makeInVisible()

                var c = 0

                for(i in listScheduleFertiliztion) {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        textView.text = day.day.toString()

                        if (day.date.isBefore(today)) {
                            textView.setTextColorRes(R.color.color_title_home)
                        } else {
                            when {
                                i.startDate == day.date && i.endDate == null -> {
                                    textView.setTextColorRes(R.color.white)
                                    roundBgView.makeVisible()
                                    roundBgView.setBackgroundResource(R.drawable.example_4_single_selected_bg)
                                }
                                day.date == i.startDate -> {
                                    textView.setTextColorRes(R.color.white)
                                    textView.background = startBackground
                                }
                                i.startDate != null && i.endDate != null && (day.date > i.startDate && day.date < i.endDate) -> {
                                    textView.setTextColorRes(R.color.white)
                                    textView.setBackgroundResource(R.drawable.example_4_continuous_selected_bg_middle)
                                }
                                day.date == i.endDate -> {
                                    textView.setTextColorRes(R.color.white)
                                    textView.background = endBackground
                                }
                                day.date == today -> {
//                                    textView.setTextColorRes(R.color.color_title_home)
                                    roundBgView.makeVisible()
                                    roundBgView.setBackgroundResource(R.drawable.example_4_today_bg)
                                }
                                else ->  {
                                    when(day.date) {
//                                        selectedDate -> {
//                                            textView.setTextColorRes(R.color.white)
//                                            textView.setBackgroundResource(R.drawable.example_2_selected_bg)
//                                        }
//                                        else -> {
//                                            textView.setTextColorRes(R.color.color_title_home)
//                                            textView.background = null
//                                        }
                                    }
                                    if(c==0)
                                        textView.setTextColorRes(R.color.color_title_home)
                                }
                            }
                        }
                    } else {

                        // This part is to make the coloured selection background continuous
                        // on the blank in and out dates across various months and also on dates(months)
                        // between the start and end dates if the selection spans across multiple months.

                        val startDate = i.startDate
                        val endDate = i.endDate
                        if (startDate != null && endDate != null) {
                            // Mimic selection of inDates that are less than the startDate.
                            // Example: When 26 Feb 2019 is startDate and 5 Mar 2019 is endDate,
                            // this makes the inDates in Mar 2019 for 24 & 25 Feb 2019 look selected.
                            if ((day.owner == DayOwner.PREVIOUS_MONTH &&
                                        startDate.monthValue == day.date.monthValue &&
                                        endDate.monthValue != day.date.monthValue) ||
                                // Mimic selection of outDates that are greater than the endDate.
                                // Example: When 25 Apr 2019 is startDate and 2 May 2019 is endDate,
                                // this makes the outDates in Apr 2019 for 3 & 4 May 2019 look selected.
                                (day.owner == DayOwner.NEXT_MONTH &&
                                        startDate.monthValue != day.date.monthValue &&
                                        endDate.monthValue == day.date.monthValue) ||

                                // Mimic selection of in and out dates of intermediate
                                // months if the selection spans across multiple months.
                                (startDate < day.date && endDate > day.date &&
                                        startDate.monthValue != day.date.monthValue &&
                                        endDate.monthValue != day.date.monthValue)
                            ) {
                                textView.setBackgroundResource(R.drawable.example_4_continuous_selected_bg_middle)
                            }
                        }
                    }
                    c++
                }

            }
        }
    }

    fun Context.getDrawableCompat(@DrawableRes drawable: Int) = ContextCompat.getDrawable(this, drawable)

    fun GradientDrawable.setCornerRadius(
        topLeft: Float = 0F,
        topRight: Float = 0F,
        bottomRight: Float = 0F,
        bottomLeft: Float = 0F
    ) {
        cornerRadii = arrayOf(
            topLeft, topLeft,
            topRight, topRight,
            bottomRight, bottomRight,
            bottomLeft, bottomLeft
        ).toFloatArray()
    }

    fun daysOfWeekFromLocale(): Array<DayOfWeek> {
//        val firstDayOfWeek = WeekFields.of(Locale/.getDefault()).firstDayOfWeek
        val firstDayOfWeek = DayOfWeek.MONDAY
        var daysOfWeek = DayOfWeek.values()
        // Order `daysOfWeek` array so that firstDayOfWeek is at index 0.
        // Only necessary if firstDayOfWeek != DayOfWeek.MONDAY which has ordinal 0.
        if (firstDayOfWeek != DayOfWeek.MONDAY) {
            val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
            val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
            daysOfWeek = rhs + lhs
        }
        return daysOfWeek
    }

    fun TextView.setTextColorRes(@ColorRes color: Int) = setTextColor(context.getColorCompat(color))

    fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)

    fun View.makeVisible() {
        visibility = View.VISIBLE
    }

    fun View.makeInVisible() {
        visibility = View.INVISIBLE
    }

    private class DayViewContainer(view: View, listScheduleFertilization: MutableList<ScheduleFertilization>, clvScheduleFertilization: CalendarView) : ViewContainer(view) {
        lateinit var day: CalendarDay // Will be set when this container is bound.
        val binding = Example4CalendarDayBinding.bind(view)
        var selectedDate : LocalDate ?= null

        init {
            view.setOnClickListener {
                Toast.makeText(view.context, "${day.date.dayOfYear} ${day.date.month}" , Toast.LENGTH_SHORT).show()
                if (day.owner == DayOwner.THIS_MONTH) {
                    if (selectedDate == day.date) {
                        clvScheduleFertilization.notifyDayChanged(day)
                    } else {
                        val oldDate = selectedDate
                        selectedDate = day.date
                        clvScheduleFertilization.notifyDateChanged(day.date)
                        oldDate?.let { clvScheduleFertilization.notifyDateChanged(oldDate) }
                    }
                }
            }
        }
    }
}