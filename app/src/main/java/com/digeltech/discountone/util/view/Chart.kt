package com.digeltech.discountone.util.view

import android.graphics.Color
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.digeltech.discountone.R
import com.digeltech.discountone.domain.model.Price
import com.digeltech.discountone.util.time.convertUnixToDate
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

fun drawCharts(prices: List<Price>, chart: LineChart, marker: CardView) {
    val entries = prices.mapIndexed { index, price -> Entry(index.toFloat(), price.price) }
    val dataSet = LineDataSet(entries, "null")

    dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
    dataSet.circleRadius = 6f
    dataSet.circleHoleRadius = 4f
    dataSet.cubicIntensity = 0.05f

    dataSet.color = Color.BLUE
    dataSet.valueTextColor = Color.TRANSPARENT
    dataSet.setCircleColor(Color.BLUE)
    dataSet.fillDrawable = chart.getImageDrawable(R.drawable.gradient_chart)
    dataSet.setDrawFilled(true)

    chart.description = null
    chart.legend.isEnabled = false
    chart.isScaleXEnabled = true
    chart.isScaleYEnabled = false
    chart.xAxis.isEnabled = false
    chart.axisLeft.setDrawLabels(false)
    chart.axisRight.valueFormatter = RupeeValueFormatter()
    chart.axisRight.textColor = chart.getColorValue(R.color.defaultColor)

    chart.data = LineData(dataSet)

    chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
        override fun onValueSelected(e: Entry?, h: Highlight?) {
            if (e != null) {
                marker.visible()
                val tvChartPrice = marker.findViewById<TextView>(R.id.tvChartPrice)
                val tvChartDate = marker.findViewById<TextView>(R.id.tvChartDate)
                tvChartPrice.text = "₹${e.y.toInt()}"
                val unixDate = prices[e.x.toInt()].date
                tvChartDate.text = convertUnixToDate(unixDate)
            }
        }

        override fun onNothingSelected() {
            marker.invisible()
        }
    })

    chart.animateX(2000)

}

class RupeeValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return "₹${value.toInt()}"
    }
}