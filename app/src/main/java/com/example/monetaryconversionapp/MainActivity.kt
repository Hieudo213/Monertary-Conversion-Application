package com.example.monetaryconversionapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.ComponentActivity


class MainActivity : ComponentActivity() {
    private val exchangeRates = mapOf(
        "VND" to 24000f,
        "USD" to 1f,
        "EUR" to 0.9f
    )

    private fun convertCurrency(amount: Float, from: String, to: String): Float {
        val rateFrom = exchangeRates[from] ?: 1f
        val rateTo = exchangeRates[to] ?: 1f
        return amount / rateFrom * rateTo
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var currentEditText: EditText? = null
        var isProgrammaticChange = false

        val inputCurrency = findViewById<EditText>(R.id.input_currency)
        val outputCurrency = findViewById<EditText>(R.id.output_currency)

        inputCurrency.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) currentEditText = inputCurrency
        }

        val spinnerFrom: Spinner = findViewById(R.id.spinner_from)
        val spinnerTo: Spinner = findViewById(R.id.spinner_to)

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.currency_options,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerFrom.adapter = adapter
        spinnerTo.adapter = adapter

        val buttonIds = intArrayOf(
            R.id.button_number_0, R.id.button_number_1, R.id.button_number_2,
            R.id.button_number_3, R.id.button_number_4, R.id.button_number_5,
            R.id.button_number_6, R.id.button_number_7, R.id.button_number_8,
            R.id.button_number_9
        )

        for (id in buttonIds) {
            val btn = findViewById<Button>(id)
            btn.setOnClickListener {
                val number = btn.text.toString()
                currentEditText?.let {
                    val current = it.text.toString()
                    if (current == "0") {
                        it.setText(number)
                    } else {
                        it.setText(current + number)
                    }
                }
            }
        }

        findViewById<Button>(R.id.button_delete).setOnClickListener {
            currentEditText?.let {
                val current = it.text.toString()
                it.setText(if (current.length > 1) current.dropLast(1) else "0")
            }
        }

        findViewById<Button>(R.id.button_delete_all).setOnClickListener {
            currentEditText?.setText("0")
        }

        inputCurrency.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isProgrammaticChange) return
                val value = s.toString().toFloatOrNull()
                if (value != null) {
                    val fromCurrency = spinnerFrom.selectedItem.toString()
                    val toCurrency = spinnerTo.selectedItem.toString()
                    val result = convertCurrency(value, fromCurrency, toCurrency)
                    Log.d("MyApp", "Giá trị sau chuyển đổi: $result")

                    isProgrammaticChange = true
                    outputCurrency.setText(String.format("%.2f", result))
                    isProgrammaticChange = false
                } else {
                    isProgrammaticChange = true
                    outputCurrency.setText("Lỗi!")
                    isProgrammaticChange = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        outputCurrency.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && !isProgrammaticChange) {
                val outputText = outputCurrency.text.toString()
                val amount = outputText.toFloatOrNull()

                if (amount != null) {
                    val fromCurrency = spinnerTo.selectedItem.toString()
                    val toCurrency = spinnerFrom.selectedItem.toString()
                    val result = convertCurrency(amount, fromCurrency, toCurrency)
                    Log.d("ConvertResult", "Chuyển $amount $fromCurrency -> $toCurrency = $result")

                    isProgrammaticChange = true
                    inputCurrency.setText(String.format("%.2f", result))
                    isProgrammaticChange = false
                } else {
                    isProgrammaticChange = true
                    inputCurrency.setText("Lỗi!")
                    isProgrammaticChange = false
                }
            }
        }
    }
}
