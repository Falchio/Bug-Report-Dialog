package com.example.sendbugreport

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner

class BugReportDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bug_report_dialog)
        val spinner: Spinner = findViewById(R.id.spinner)
        val list = listOf("Сейчас", "Новостная лента")
        val adapter =
            ArrayAdapter(this.context,  android.R.layout.simple_spinner_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

    }
}