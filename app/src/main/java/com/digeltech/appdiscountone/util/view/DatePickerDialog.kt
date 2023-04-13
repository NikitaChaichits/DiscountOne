package com.digeltech.appdiscountone.util.view

import android.app.DatePickerDialog
import android.content.Context
import java.util.*

fun showDatePickerDialog(context: Context, listener: DatePickerDialog.OnDateSetListener) {
    val calendar: Calendar = Calendar.getInstance()
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val month = calendar.get(Calendar.MONTH)
    val year = calendar.get(Calendar.YEAR)
    val dpd = DatePickerDialog(context, listener, year, month, day)
    dpd.show()
}