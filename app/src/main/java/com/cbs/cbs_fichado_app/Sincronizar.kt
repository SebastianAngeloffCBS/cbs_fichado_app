package com.cbs.cbs_fichado_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.cbs.cbs_fichado_app.databinding.ActivityListadoFichadosBinding
import com.cbs.cbs_fichado_app.databinding.ActivitySincronizarBinding
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.json.JSONException
import java.util.HashMap

class Sincronizar : AppCompatActivity() {

    private lateinit var binding: ActivitySincronizarBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySincronizarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.gifcarga.isVisible = false
        binding.txtsincronizar.isVisible = false


        cargalistview()

    }

    fun  cargalistview() {

        val admin = AdminSQLiteOpenHelper(this, "dbfichado", null, 1)
        val bd = admin.writableDatabase
        val fila = bd.rawQuery("select * from fichado where sincronizado = 'no' ", null)

        if (fila.moveToFirst()) {

            val arrayValues: ArrayList<String> = ArrayList()

            do {

                arrayValues.add("DNI: "+fila.getString(1)+ " | FECHA "+fila.getString(3)
                        +"\n"+"PERSONA: "+fila.getString(2)+"\n"
                        +"CICLO: "+fila.getString(6)+ " | SINCRONIZADO: "+fila.getString(5)+"\n")

            } while (fila.moveToNext())


            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayValues)
            binding.listadofichados.adapter = adapter

            bd.close()


        } else {

            Toast.makeText(
                this,
                "Sin datos",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    fun  pantallaEspera(){

        binding.txtTitulo.isVisible = false
        binding.btnSincronizar.isVisible = false
        binding.volver.isVisible = false
        binding.listadofichados.isVisible = false
        binding.gifcarga.isVisible = true
        binding.txtsincronizar.isVisible = true




    }



    fun sincronizar(view: View) {

        //Oculto los elementos de la vista y muestro un gif de carga

        pantallaEspera()


        //Busco los registros en estado no sincronizado
        val admin = AdminSQLiteOpenHelper(this, "dbfichado", null, 1)
        val bd = admin.writableDatabase
        val fila = bd.rawQuery("select * from fichado where sincronizado = 'no' ", null)

        if (fila.moveToFirst()) {

            val arrayValues: ArrayList<String> = ArrayList()

            do {

                var id: String = fila.getString(0)

                Toast.makeText(this, id, Toast.LENGTH_SHORT)
                    .show()

                //insertaservidor(id)

            } while (fila.moveToNext())

            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayValues)
            binding.listadofichados.adapter = adapter

            bd.close()

        }

    }


    fun insertaservidor(Id : String){

        val url = "http://ws.grupocbs.com.ar/api/Fichado/ActualizaFichado"

        val queue = Volley.newRequestQueue(this)

        val request: StringRequest = object : StringRequest(
            Method.POST,
            url,
            Response.Listener { response ->
                try
                {
                    val mapper = jacksonObjectMapper()

                    val userData: Boolean = mapper.readValue(response)

                    if (userData)
                    {
                        Toast.makeText(this, "REGISTRO ACTUALIZADO", Toast.LENGTH_SHORT)
                            .show()

                    }
                    else
                    {
                        Toast.makeText(this, "OPERACIÓN INCORRECTA", Toast.LENGTH_SHORT)
                            .show()

                    }

                }
                catch (e: JSONException)
                {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error -> // method to handle errors.
                Toast.makeText(
                    this,
                    "Fail to get response = $error",
                    Toast.LENGTH_SHORT
                ).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["id"] = Id
                return params
            }
        }
        queue.add(request)

    }


    fun volver(view: View) {
        val inicio = Intent(this, Fichado()::class.java)
        startActivity(inicio)
    }


}