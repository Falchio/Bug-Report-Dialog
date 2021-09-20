package com.example.sendbugreport.bug_report

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.ColorFilter
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import com.example.sendbugreport.R
import java.util.concurrent.TimeUnit.SECONDS

class BugReportDialog(context: Context) : Dialog(context) {
    private val listOfScreens = this.context.resources.getStringArray(R.array.names_of_screens)
    private lateinit var button: Button
    private lateinit var spinner: Spinner
    private lateinit var header: TextView
    private lateinit var editText: TextView
    private val bugReport = BugReport()
    private val twoSeconds = SECONDS.toMillis(2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bug_report_dialog)
        header = findViewById(R.id.bug_report_header)
        editText = findViewById(R.id.bug_report_edit_text)
        setupSpinner()

        button = findViewById(R.id.bug_report_next_button)
        button.visibility = View.GONE
        button.setOnClickListener { firstStepClickListener() }
    }

    private fun firstStepClickListener() {
        button.visibility = View.GONE
        button.setOnClickListener { secondStepClickListener() }
        buttonVisibilityCountDownTimer(twoSeconds, twoSeconds).start()
        spinner.visibility = View.GONE
        editText.visibility = View.VISIBLE
        editText.requestFocus()
        header.text = "Что именно работает не так? Пожалуйста опишите проблему как можно подробнее."
    }

    private fun secondStepClickListener() {
        if (editText.text.length < 10) {
            Toast.makeText(
                this.context,
                "Введите не менее 10 символов, для описания проблемы, пожалуйста.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        header.text = "Как именно должно работать на Ваш взгляд?"
        bugReport.actual = editText.text.toString()
        editText.text = ""
        button.visibility = View.GONE
        button.setOnClickListener { thirdStepClickListener() }
        buttonVisibilityCountDownTimer(twoSeconds, twoSeconds).start()
    }

    private fun thirdStepClickListener() {
        bugReport.expected = editText.text.toString()

        try {
            sendMail(bugReport.toString(), "userId")
        } catch (exception: ActivityNotFoundException) {
            Toast.makeText(
                this.context,
                "нет почтового клиента",
                Toast.LENGTH_LONG
            ).show()
        } catch (exception: Exception) {
            exception.printStackTrace()
        } finally {
            this@BugReportDialog.dismiss()
        }
    }


    private fun sendMail(mailBody: String, userId: String) {
        val emailIntent = Intent(Intent.ACTION_SEND)
        val emailSelectorIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
        emailIntent.selector = emailSelectorIntent
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("supp.pa.autocomm@gmail.com"))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Bug Report" + userId)
        emailIntent.putExtra(Intent.EXTRA_TEXT, mailBody)
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        startActivity(this.context, emailIntent, null)
    }

    private fun setupSpinner() {
        val adapter =
            ArrayAdapter(this.context, android.R.layout.simple_spinner_item, listOfScreens)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner = findViewById(R.id.bug_report_spinner)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = onItemSelectedListener()
    }

    private fun buttonVisibilityCountDownTimer(millisInFuture: Long, countDownInterval: Long) =
        object : CountDownTimer(millisInFuture, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                button.visibility = View.VISIBLE
            }

        }

    private fun onItemSelectedListener() = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {

            buttonVisibilityCountDownTimer(twoSeconds, twoSeconds).start()
            bugReport.screen = listOfScreens[position]
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

    }

    private class BugReport() {
        var screen = ""
        var expected = ""
        var actual = ""

        override fun toString(): String {
            return "BugReport - [screen: $screen, expected: $expected, actual: $actual]"
        }
    }
}