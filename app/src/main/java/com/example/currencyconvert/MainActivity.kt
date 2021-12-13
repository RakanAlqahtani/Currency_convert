package com.example.currencyconvert

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        setDropdwon()
        var item = "jpy"
        autoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedItem = parent.getItemAtPosition(position).toString()
                item = selectedItem
//            println("!!!!! $selectedItem")
                // Display the clicked item using toast
                Toast.makeText(applicationContext, "Selected : $selectedItem", Toast.LENGTH_SHORT)
                    .show()
            }

        btnConvert.setOnClickListener {
            requestAPI(item)

        }

//        autoCompleteTextView.setOnItemClickListener{
//            println("!!!!!!! ${autoCompleteTextView.text}")
//        }
    }

    private fun requestAPI(item: String) {

        CoroutineScope(IO).launch {

            val data = async { fetchData() }.await()

            if (data.isNotEmpty()) {

                updateResult(data, item)
            }

        }
    }

    private suspend fun updateResult(result: String, item: String) {

        withContext(Main) {

            val jsonArray = JSONObject(result)

            val date = jsonArray.getString("date")
            tvDate.text = "Date: ${date}"

            val eur = jsonArray.getJSONObject("eur")
            val i  = eur[item].toString()
            if (etdText.text.isNotEmpty()) {
                val result = etdText.text.toString().toDouble() * i.toDouble()

                tvResult.text = "Result : $result"

            }

                else{
                    Toast.makeText(applicationContext, "Enter number", Toast.LENGTH_SHORT).show()
                }



        }
    }




    private fun fetchData(): String {

        var response = ""

        try {
            response =
                URL("https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies/eur.json").readText()
        } catch (e: Exception) {

            print("issuu $e")
        }

        return response
    }

    private fun setDropdwon() {
        val cur_Drop = resources.getStringArray(R.array.currency)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, cur_Drop)
        autoCompleteTextView.setAdapter(arrayAdapter)

//        println("!!!!!!! ${autoCompleteTextView.}")
    }


}


