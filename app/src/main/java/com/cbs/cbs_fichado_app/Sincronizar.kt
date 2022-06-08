package com.cbs.cbs_fichado_app

import android.content.ContentValues
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
        binding.imageView2.isVisible = false
        binding.txtderechos.isVisible = false
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
        binding.imageView2.isVisible = true
        binding.txtderechos.isVisible = true
        binding.gifcarga.isVisible = true
        binding.txtsincronizar.isVisible = true
    }

    fun  pantallaInicio(){
        binding.txtTitulo.isVisible = true
        binding.btnSincronizar.isVisible = true
        binding.volver.isVisible = true
        binding.listadofichados.isVisible = true
        binding.imageView2.isVisible = false
        binding.txtderechos.isVisible = false
        binding.gifcarga.isVisible = false
        binding.txtsincronizar.isVisible = false
        cargalistview()
    }


    fun sincronizar(view: View) {

        pantallaEspera()

        //Busco los registros en estado no sincronizado
        val admin = AdminSQLiteOpenHelper(this, "dbfichado", null, 1)
        val bd = admin.writableDatabase
        val fila = bd.rawQuery("select * from fichado where sincronizado = 'no' ", null)

        if (fila.moveToFirst()) {

            val arrayValues: ArrayList<String> = ArrayList()

            do {

                var id : String = fila.getString(0)
                var dni: String = fila.getString(1)
                var nombre: String = fila.getString(2)
                var fechahora: String = fila.getString(3)
                var dimension: String = fila.getString(4)
                var ciclo: String = fila.getString(6)

                insertaservidor(id,dni,nombre,fechahora,dimension,ciclo)

            } while (fila.moveToNext())

            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayValues)
            binding.listadofichados.adapter = adapter

            bd.close()

        }

        pantallaInicio()

    }

    fun insertaservidor(Id: String,dni : String, nombre : String, fechahora : String, dimension : String, ciclo : String){

        //Me conecto al servidor para validar la persona
        val url = "http://ws.grupocbs.com.ar/api/Fichado/AltaFichado"

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
                        Toast.makeText(this, "OPERACIÓN CORRECTA", Toast.LENGTH_SHORT)
                            .show()

                        //Si se inserto en el servidor se debe poder actualizar el registro
                        actualizaDatoLocal(Id)

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
                params["dni"] = dni
                params["nombre"] = nombre
                params["dimension"] = dimension
                params["fechaHora"] = fechahora
                params["ciclo"] = ciclo
                return params
            }
        }
        queue.add(request)

    }


    fun actualizaDatoLocal(Id : String){

        //Update al registro
        val admin = AdminSQLiteOpenHelper(this, "dbfichado", null, 1)
        val bd = admin.writableDatabase
        val registro = ContentValues()
        registro.put("sincronizado", "si")
        val cant = bd.update("fichado", registro, "Id=${Id}", null)
        bd.close()

    }

    fun volver(view: View) {
        val inicio = Intent(this, Fichado()::class.java)
        startActivity(inicio)
    }

}