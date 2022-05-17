package com.cbs.cbs_fichado_app

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun conectar(view: View) {

         var IMEI  = findViewById<EditText>(R.id.editTextNumberPassword).text.toString()
         sendPost(IMEI)

        //Si el WS se valido correctamente se pasa a la segunda pantalla
        // val PantallaFichado = Intent(this, Fichado::class.java)
        // startActivity(PantallaFichado)


    }

    fun sendPost(IMEI:String) {

        //Envio por Get
//        val textView = findViewById<TextView>(R.id.txtderechos)
//        val queue = Volley.newRequestQueue(this)
//        val url = "http://www.google.com"
//        val stringRequest = StringRequest(
//        Request.Method.GET,
//        url,
//        Response.Listener<String> { response -> textView.text = "Response is: ${response.substring(0, 500)}" },
//        Response.ErrorListener { textView.text = "That didn't work!" })
//        queue.add(stringRequest)

//        val queue = Volley.newRequestQueue(this)

        val url = "http://ws.grupocbs.com.ar/API/TELEFONOS/GetTelefono"

//        val paramJson = JSONObject()
//
//        paramJson.put("IMEI", "355969783220715")
        //paramJson.put("key2", "value2")



        val queue = Volley.newRequestQueue(this@MainActivity)


        val request: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->

                Toast.makeText(this@MainActivity, "Data added to API", Toast.LENGTH_SHORT).show()
                try {

                    val respObj = JSONObject(response)

                    //val name = respObj.getString("name")


                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error -> // method to handle errors.
                Toast.makeText(
                    this@MainActivity,
                    "Fail to get response = $error",
                    Toast.LENGTH_SHORT
                ).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()

                params["IMEI"] = "355969783220715"

                return params
            }
        }
        queue.add(request)





    }















}