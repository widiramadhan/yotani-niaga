package com.javaindoku.yotaniniaga.utilities.customchart;

import android.content.Context;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.javaindoku.yotaniniaga.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by philipp on 02/06/16.
 */
public class DayAxisValueFormatter extends ValueFormatter
{
    private Context context;
    private final String[] mMonths = new String[]{
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    private final BarLineChartBase<?> chart;

    public DayAxisValueFormatter(BarLineChartBase<?> chart, Context context) {
        this.chart = chart;
        this.context = context;
    }

    @Override
    public String getFormattedValue(float value) {

        int month = (int) value;
        List<String> months = new ArrayList<>();
        months.add(context.getString(R.string.srJan));
        months.add(context.getString(R.string.srFeb));
        months.add(context.getString(R.string.srMar));
        months.add(context.getString(R.string.srApr));
        months.add(context.getString(R.string.srMay));
        months.add(context.getString(R.string.srJun));
        months.add(context.getString(R.string.srJul));
        months.add(context.getString(R.string.srAug));
        months.add(context.getString(R.string.srSep));
        months.add(context.getString(R.string.srOct));
        months.add(context.getString(R.string.srNov));
        months.add(context.getString(R.string.srDes));


//        int year = determineYear(days);

//        int month = determineMonth(days);
        return months.get(month-1);
//        return monthName;
//        String yearName = String.valueOf(year);
//
//        if (chart.getVisibleXRange() > 30 * 6) {
//
//            return monthName + " " + yearName;
//        } else {
//
//            int dayOfMonth = determineDayOfMonth(days, month + 12 * (year - 2016));
//
//            String appendix = "th";
//
//            switch (dayOfMonth) {
//                case 1:
//                    appendix = "st";
//                    break;
//                case 2:
//                    appendix = "nd";
//                    break;
//                case 3:
//                    appendix = "rd";
//                    break;
//                case 21:
//                    appendix = "st";
//                    break;
//                case 22:
//                    appendix = "nd";
//                    break;
//                case 23:
//                    appendix = "rd";
//                    break;
//                case 31:
//                    appendix = "st";
//                    break;
//            }
//
//            return dayOfMonth == 0 ? "" : dayOfMonth + appendix + " " + monthName;
//        }
    }

    private int getDaysForMonth(int month, int year) {

        // month is 0-based

        if (month == 1) {
            boolean is29Feb = false;

            if (year < 1582)
                is29Feb = (year < 1 ? year + 1 : year) % 4 == 0;
            else if (year > 1582)
                is29Feb = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);

            return is29Feb ? 29 : 28;
        }

        if (month == 3 || month == 5 || month == 8 || month == 10)
            return 30;
        else
            return 31;
    }

    private int determineMonth(int dayOfYear) {

        int month = -1;
        int days = 0;

        while (days < dayOfYear) {
            month = month + 1;

            if (month >= 12)
                month = 0;

            int year = determineYear(days);
            days += getDaysForMonth(month, year);
        }

        return Math.max(month, 0);
    }

    private int determineDayOfMonth(int days, int month) {

        int count = 0;
        int daysForMonths = 0;

        while (count < month) {

            int year = determineYear(daysForMonths);
            daysForMonths += getDaysForMonth(count % 12, year);
            count++;
        }

        return days - daysForMonths;
    }

    private int determineYear(int days) {

        if (days <= 366)
            return 2016;
        else if (days <= 730)
            return 2017;
        else if (days <= 1094)
            return 2018;
        else if (days <= 1458)
            return 2019;
        else
            return 2020;

    }
}
