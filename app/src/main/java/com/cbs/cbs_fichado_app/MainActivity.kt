package com.cbs.cbs_fichado_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun conectar(view: View) {

        // var IMEI  = findViewById<EditText>(R.id.editTextNumberPassword).text.toString()
        // sendPost(IMEI)

        //Si el WS se valido correctamente se pasa a la segunda pantalla
        val PantallaFichado = Intent(this, Fichado::class.java)
        startActivity(PantallaFichado)


    }

    fun sendPost(IMEI:String) {

        //Define url para realizar peticion POST.
        var urlPost = "http://ws.grupocbs.com.ar/API/TELEFONOS"

        //Concatena y codifica parámetros.
        var reqParam = URLEncoder.encode("IMEI", "UTF-8") + "=" + URLEncoder.encode(IMEI, "UTF-8")


        val mURL = URL(urlPost)

        with(mURL.openConnection() as HttpURLConnection) {
            //Define metodo
            requestMethod = "POST"

            val wr = OutputStreamWriter(getOutputStream());
            wr.write(reqParam);
            wr.flush();

            println(requestMethod + "URL : $url")
            println(requestMethod + "Response Code : $responseCode")

            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                it.close()
                //imprime respuesta.

                var mensaje : String = "Respuesta : $response"

                // Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()


            }
        }
    }


}