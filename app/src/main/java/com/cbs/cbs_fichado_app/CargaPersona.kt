package com.cbs.cbs_fichado_app

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.cbs.cbs_fichado_app.databinding.ActivityCargaPersonaBinding
import com.cbs.cbs_fichado_app.databinding.ActivityValidacionPersonaBinding
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.json.JSONException

class CargaPersona : AppCompatActivity() {

    private lateinit var binding: ActivityCargaPersonaBinding
    private var Documento: String = ""
    private var Nombre: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCargaPersonaBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.progressBar.isVisible = false
        binding.btnGuardarPersona.isVisible = false
        binding.lbltitulo2.isVisible = false
        binding.lbltitulo3.isVisible = false




    }

    fun buscapersona(view: View) {
        binding.progressBar.isVisible = true
        binding.btnBuscarPersona.isVisible = false

        var dni: String = binding.txtdni.text.toString()
        validaPersonal(dni)
    }


    fun volver(view: View) {
        val listado = Intent(this, ListadoPersonal()::class.java)
        startActivity(listado)
    }

    fun validaPersonal(dni : String){

        //Me conecto al servidor para validar la persona
        val url = "http://ws.grupocbs.com.ar/api/Fichado/VerificaDocumentoUsuario"

        val queue = Volley.newRequestQueue(this)

        val request: StringRequest = object : StringRequest(
            Method.POST,
            url,
            Response.Listener { response ->
                try
                {
                    val mapper = jacksonObjectMapper()

                    val userData: Personal = mapper.readValue(response)

                    if (userData.codemp != "0")
                    {

                        //Cargo los datos en la interfaz y en un objeto en memoria
                        binding.lbltitulo2.isVisible = true
                        binding.lbltitulo3.isVisible = true
                        binding.btnGuardarPersona.isVisible = true

                        binding.btnBuscarPersona.isVisible = true

                        binding.progressBar.isVisible = false


                        binding.lblnombreapellido.setText(userData.nombreapellido)

                        Documento = userData.dni.toString()
                        Nombre = userData.nombreapellido.toString()

//                        val validarPersona = Intent(this, ValidacionPersona()::class.java)
//
//                        validarPersona.putExtra("Persona", userData.nombreapellido)
//
//                        startActivity(validarPersona)

                    }
                    else
                    {
                        var progressBar = findViewById<ProgressBar>(R.id.progressBar)
                        progressBar.isVisible = false
                        var boton = findViewById<Button>(R.id.btn_conectar)
                        boton.isVisible = true


                        Toast.makeText(this, "DNI INCORRECTO", Toast.LENGTH_SHORT)
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

                return params
            }
        }
        queue.add(request)

    }

    fun guardar(view: View) {

        val admin = AdminSQLiteOpenHelper(this,"administracion", null, 1)
        val bd = admin.writableDatabase
        val registro = ContentValues()
        registro.put("dni", Documento)
        registro.put("nombre", Nombre)
        bd.insert("persona", null, registro)
        bd.close()

        val listado = Intent(this, ListadoPersonal()::class.java)
        listado.putExtra("mensaje", "si")

        startActivity(listado)

    }


}