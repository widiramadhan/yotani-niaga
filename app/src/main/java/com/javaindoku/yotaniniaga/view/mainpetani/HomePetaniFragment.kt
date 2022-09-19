package com.javaindoku.yotaniniaga.view.mainpetani

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.components.Legend.LegendForm
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.Utils
import com.google.android.material.shape.CornerFamily
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseFragment
import com.javaindoku.yotaniniaga.databinding.FragmentHomePetaniBinding
import com.javaindoku.yotaniniaga.model.news.NewsData
import com.javaindoku.yotaniniaga.utilities.customchart.DayAxisValueFormatter
import com.javaindoku.yotaniniaga.utilities.customchart.XYMarkerView
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import com.javaindoku.yotaniniaga.view.listpricetbs.ListPriceTbsActivity
import com.javaindoku.yotaniniaga.view.mainkoperasi.MainKoperasiActivity
import com.javaindoku.yotaniniaga.view.mainkoperasi.adapter.NewsHomeAdapter
import com.javaindoku.yotaniniaga.view.news.NewsActivity
import com.javaindoku.yotaniniaga.view.news.adapter.NewsAdapter
import com.javaindoku.yotaniniaga.view.schedulefertilization.ScheduleFertilizationActivity
import java.util.*


class HomePetaniFragment : BaseFragment() {
    private lateinit var binding: FragmentHomePetaniBinding
    private lateinit var set1: LineDataSet
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home_petani, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val radius = resources.getDimension(R.dimen._30sdp)
        binding.shpHeaderHome.shapeAppearanceModel = binding.shpHeaderHome.shapeAppearanceModel.toBuilder().setBottomLeftCorner(
            CornerFamily.ROUNDED, radius)
            .setBottomRightCorner(CornerFamily.ROUNDED, radius)
            .build()

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 45)

        binding.rcyNewsHome.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val listNews = listOf<NewsData>(
//            NewsData(1, "Indonesia adalah negara dengan berbagai macam kearifan lokal", "2020-08-21 15:48:33 GMT+07:00", "https://ci5.googleusercontent.com/proxy/mJfD7nMlcO_OaeYD6lWd1Fm8movsyqrQeb7bU9TIneK1Rx8tTS8L0qA_qolvMvelk3hNO_fBsJ5TL-65nKKlx7ca94bKIplYclncFF7cQEwdSbhuqm1o9iiA=s0-d-e1-ft#https://www.bca.co.id/~/media/Images/e-info2020/20200812-elc-slice1.jpg"),
//            NewsData(2, "Indonesia adalah berdaulat adil dan makmur", "2020-08-21 15:48:33 GMT+07:00", "https://ssl.gstatic.com/ui/v1/icons/mail/rfr/logo_gmail_lockup_default_1x.png")
        )

        val adapter = NewsHomeAdapter(listNews, object: NewsAdapter.OnClickNews{
            override fun toDetailNews(newsData: NewsData) {

            }
        })
        binding.rcyNewsHome.adapter = adapter

//        val listGarden = listOf(
//            Garden("Kebun 1", "1 Ton", "1000", "2013", "1 Hektar", "Bukit asam haliman", numberOfPalmSupplied = "1000"),
//            Garden("Kebun 2", "1 Ton", "1000", "2013", "1 Hektar", "Bukit asam haliman", numberOfPalmSupplied = "1000")
//        )
//        val adapterGarden = GardenAdapter(listGarden, object : GardenAdapter.OnClickGarden {
//            override fun toEditGarden(pks: Garden) {
//
//            }
//        })
//        binding.rcyGardenHome.layoutManager = LinearLayoutManager(requireContext())
//        binding.rcyGardenHome.adapter = adapterGarden

        binding.lblSeeAllNews.setOnSafeClickListener {
            startActivity(Intent(activity, NewsActivity::class.java))
        }
        binding.lytPriceTbs.setOnSafeClickListener {
            startActivity(Intent(activity, ListPriceTbsActivity::class.java))
        }

        binding.lytScheduleFertilization.setOnSafeClickListener {
            startActivity(Intent(activity, ScheduleFertilizationActivity::class.java))
        }

        binding.lytScheduleNursery.setOnSafeClickListener {
            (activity as MainKoperasiActivity).updateLocale()
        }
    }

    private fun createGraph() {
        binding.chartHome.setBackgroundColor(Color.WHITE)

        // disable description text

        // disable description text
        binding.chartHome.getDescription().setEnabled(false)

        // enable touch gestures

        // enable touch gestures
        binding.chartHome.setTouchEnabled(true)

        // set listeners

        // set listeners
//        binding.chartHome.setOnChartValueSelectedListener(this)
        binding.chartHome.setDrawGridBackground(false)

//            // create marker to display box when values are selected
//            MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
//
//            // Set the marker to the chart
//            mv.setChartView(chart);
//            chart.setMarker(mv);


//            // create marker to display box when values are selected
//            MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
//
//            // Set the marker to the chart
//            mv.setChartView(chart);
//            chart.setMarker(mv);
        val xAxisFormatter: ValueFormatter =
            DayAxisValueFormatter(binding.chartHome, requireActivity())
//        val mv = XYMarkerView(activity, xAxisFormatter)
        val mv = XYMarkerView(activity, xAxisFormatter)
        mv.setChartView(binding.chartHome) // For bounds control

        binding.chartHome.setMarker(mv) // Set the marker to the chart


        // enable scaling and dragging

        // enable scaling and dragging
        binding.chartHome.setDragEnabled(true)
        binding.chartHome.setScaleEnabled(true)
        // chart.setScaleXEnabled(true);
        // chart.setScaleYEnabled(true);

        // force pinch zoom along both axis
        // chart.setScaleXEnabled(true);
        // chart.setScaleYEnabled(true);

        // force pinch zoom along both axis
        binding.chartHome.setPinchZoom(true)

        val xAxis: XAxis = binding.chartHome.xAxis
        xAxis.isEnabled = true
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = MonthValueFormatter(requireActivity())
        xAxis.setDrawGridLines(false)

        val yAxis: YAxis = binding.chartHome.axisLeft

        // disable dual axis (only use LEFT axis)
        binding.chartHome.getAxisRight().setEnabled(false)

        // horizontal grid lines
        yAxis.enableGridDashedLine(10f, 10f, 0f)

        // axis range
        yAxis.axisMaximum = 20f
        yAxis.axisMinimum = 0f


        val llXAxis = LimitLine(9f, "Index 10")
        llXAxis.lineWidth = 4f
        llXAxis.enableDashedLine(10f, 10f, 0f)
        llXAxis.labelPosition = LimitLabelPosition.RIGHT_BOTTOM
        llXAxis.textSize = 10f
        val ll1 = LimitLine(150f, "Upper Limit")
        ll1.lineWidth = 4f
        ll1.enableDashedLine(10f, 10f, 0f)
        ll1.labelPosition = LimitLabelPosition.RIGHT_TOP
        ll1.textSize = 10f
        val ll2 = LimitLine(-30f, "Lower Limit")
        ll2.lineWidth = 4f
        ll2.enableDashedLine(10f, 10f, 0f)
        ll2.labelPosition = LimitLabelPosition.RIGHT_BOTTOM
        ll2.textSize = 10f

        // draw limit lines behind data instead of on top
        yAxis.setDrawLimitLinesBehindData(true)
        xAxis.setDrawLimitLinesBehindData(true)

        // add limit lines
//            yAxis.addLimitLine(ll1);
//            yAxis.addLimitLine(ll2);
        //xAxis.addLimitLine(llXAxis);

        // add data
//        seekBarX.setProgress(45);
//        seekBarY.setProgress(180);

        // add data
//        seekBarX.setProgress(45);
//        seekBarY.setProgress(180);
        setData(12, 180f)

        // draw points over time

        // draw points over time
        binding.chartHome.animateX(1500)

        // get the legend (only possible after setting data)

        // get the legend (only possible after setting data)
        val l: Legend = binding.chartHome.getLegend()

        // draw legend entries as lines

        // draw legend entries as lines
        l.form = LegendForm.NONE
    }

    private fun setData(count: Int, range: Float) {
        val values =
            ArrayList<Entry>()
        for (i in 1..count) {
//            val `val` = (Math.random() * range).toFloat() - 30
            //            values.add(new Entry(i, val, getResources().getDrawable(R.drawable.star)));
            values.add(Entry(i.toFloat(), i.toFloat()))
        }
        if (binding.chartHome.getData() != null &&
            binding.chartHome.getData().getDataSetCount() > 0
        ) {
//            set1 = binding.chartHome.getData().getDataSetByIndex(0)
            set1 = binding.chartHome.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            set1.notifyDataSetChanged()
            binding.chartHome.getData().notifyDataChanged()
            binding.chartHome.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(values, "")
            set1.setDrawIcons(false)

            // draw dashed line
//            set1.enableDashedLine(10f, 5f, 0f);

            // black lines and points
            set1.color = Color.GREEN
            set1.setCircleColor(Color.GREEN)

            // line thickness and point size
            set1.lineWidth = 1f
            set1.circleRadius = 3f

            // draw points as solid circles
            set1.setDrawCircleHole(false)

            // customize legend entry
            set1.formLineWidth = 1f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set1.formSize = 15f

            // text size of values
            set1.valueTextSize = 9f

            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f)

            // set the filled area
            set1.setDrawFilled(true)
            set1.fillFormatter =
                IFillFormatter { dataSet, dataProvider -> binding.chartHome.getAxisLeft().getAxisMinimum() }

            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                val drawable = ContextCompat.getDrawable(requireActivity(), R.drawable.fade_primary)
                set1.fillDrawable = drawable
            } else {
                set1.fillColor = Color.GREEN
            }
            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the data sets

            // create a data object with the data sets
            val data = LineData(dataSets)

            // set data
            binding.chartHome.setData(data)
        }
    }

    private class MonthValueFormatter(val context: Context) :
        ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase): String {
            return if (value == 1.0f)
                context.getString(R.string.srJan)
            else if (value == 2.0f)
                context.getString(R.string.srFeb)
            else if (value == 3.0f)
                context.getString(R.string.srMar)
            else if (value == 4.0f)
                context.getString(R.string.srApr)
            else if (value == 5.0f)
                context.getString(R.string.srMay)
            else if (value == 6.0f)
                context.getString(R.string.srJun)
            else if (value == 7.0f)
                context.getString(R.string.srJul)
            else if (value == 8.0f)
                context.getString(R.string.srAug)
            else if (value == 9.0f)
                context.getString(R.string.srSep)
            else if (value == 10.0f)
                context.getString(R.string.srOct)
            else if (value == 11.0f)
                context.getString(R.string.srNov)
            else if (value == 12.0f)
                context.getString(R.string.srDes)
            else ""
        }
    }
}