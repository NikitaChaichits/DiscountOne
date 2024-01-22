package com.digeltech.discountone.util.view

import android.app.DatePickerDialog
import android.content.Context
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Calendar
import java.util.TimeZone

fun showDatePickerDialog(context: Context, listener: DatePickerDialog.OnDateSetListener) {
    val calendar: Calendar = Calendar.getInstance()
    val today = calendar.timeInMillis

    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val month = calendar.get(Calendar.MONTH)
    val year = calendar.get(Calendar.YEAR)
    val dpd = DatePickerDialog(context, listener, year, month, day)
    dpd.datePicker.maxDate = today
    dpd.show()
}

fun showMaterialDatePickerDialog(textview: TextView, fragmentManager: FragmentManager, checkIsBtnEnable: () -> Unit) {
    val constraints = CalendarConstraints.Builder()
        .setValidator(DateValidatorPointBackward.now())
        .build()

    val builder = MaterialDatePicker.Builder.datePicker()
        .setTheme(com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialCalendar)
        .setCalendarConstraints(constraints)

    val picker = builder.build()

    picker.addOnPositiveButtonClickListener { selection ->
        val calendar = Calendar.getInstance(TimeZone.getTimeZone(TimeZone.getDefault().id))
        calendar.timeInMillis = selection
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val _month = if (month < 9) "0${month + 1}" else month + 1
        val _dayOfMonth = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth

        textview.text = "$year-$_month-$_dayOfMonth"
        checkIsBtnEnable()
    }
    picker.show(fragmentManager, picker.toString())
}