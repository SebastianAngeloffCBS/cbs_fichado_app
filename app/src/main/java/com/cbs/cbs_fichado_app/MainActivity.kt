package com.cbs.cbs_fichado_app

import android.content.ContentValues
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.json.JSONException
import java.util.*


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        var progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.isVisible = false

        var btnOcultar =  findViewById<ImageButton>(R.id.btn_ocultar_pass)
        btnOcultar.isVisible = false

        val admin = AdminSQLiteOpenHelper(this, "fichadodb", null, 1)
        val bd = admin.writableDatabase
        val fila = bd.rawQuery("select * from usuario", null)
        if (fila.moveToFirst()) {

            bd.close()

            val fichado = Intent(this, Fichado::class.java)

            fichado.putExtra("UsuarioIntra", fila.getString(1))


            startActivity(fichado)

        } else {
            Toast.makeText(this, "Debe validar el usuario", Toast.LENGTH_SHORT)
                .show()
            bd.close()
        }




    }

    fun conectar(view: View) {

        view.isVisible = false

        var progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.isVisible = true

        var txtUser =
            findViewById<EditText>(R.id.txtUser).text.toString().lowercase()
        var txtPass = findViewById<EditText>(R.id.txtPass).text.toString()

        if (txtUser == "" || txtPass == "")
        {
            view.isVisible = true

            var progressBar = findViewById<ProgressBar>(R.id.progressBar)
            progressBar.isVisible = false
            Toast.makeText(this, "Debe completar el usuario y contraseña", Toast.LENGTH_SHORT)
                .show()
        }
        else
        {
            sendPost(txtUser, txtPass)

        }


    }

    fun sendPost(txtUser: String, txtPass: String) {

        val url = "http://ws.grupocbs.com.ar/api/Fichado/VerificaUsePass"

        val queue = Volley.newRequestQueue(this@MainActivity)

        val request: StringRequest = object : StringRequest(
            Method.POST,
            url,
            Response.Listener { response ->
                try
                {
                    val mapper = jacksonObjectMapper()

                    val userData: Usuario = mapper.readValue(response)

                    if (userData.idusuario != "0")
                    {
                        guardarDatos(userData)

                        sincronizaNomina()

                        val fichado = Intent(this, Fichado::class.java)

                        fichado.putExtra("UsuarioIntra", userData.usuario)

                        startActivity(fichado)
                    }
                    else
                    {
                        var progressBar = findViewById<ProgressBar>(R.id.progressBar)
                        progressBar.isVisible = false
                        var boton = findViewById<Button>(R.id.btn_conectar)
                        boton.isVisible = true


                        Toast.makeText(this, "USUARIO/CONTRASEÑA INCORRECTOS", Toast.LENGTH_SHORT)
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
                    this@MainActivity,
                    "Fail to get response = $error",
                    Toast.LENGTH_SHORT
                ).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()

                params["user"] = txtUser
                params["pass"] = txtPass

                return params
            }
        }
        queue.add(request)


    }

    fun sincronizaNomina() {

        val url = "http://ws.grupocbs.com.ar/api/Fichado/ObtenerNomina"

        val queue = Volley.newRequestQueue(this@MainActivity)

        val request: StringRequest = object : StringRequest(
            Method.POST,
            url,
            Response.Listener { response ->
                try
                {
                    val mapper = jacksonObjectMapper()

                    var personalData: ArrayList<Personal> = ArrayList()
                    personalData  = mapper.readValue(response)

                    if (personalData.count() > 0)
                    {

                        for (item in personalData){
                            val admin = AdminSQLiteOpenHelper(this,"fichadodb", null, 1)
                            val bd = admin.writableDatabase
                            val registro = ContentValues()
                            registro.put("dni", item.dni)
                            registro.put("nombre", item.nombreapellido)
                            bd.insert("persona", null, registro)
                            bd.close()
                        }

                    }
                    else
                    {
                        var progressBar = findViewById<ProgressBar>(R.id.progressBar)
                        progressBar.isVisible = false
                        var boton = findViewById<Button>(R.id.btn_conectar)
                        boton.isVisible = true

                        Toast.makeText(this, "FALLA EN SINCRONIZACIÓN NOMINA", Toast.LENGTH_SHORT)
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
                    this@MainActivity,
                    "Fail to get response = $error",
                    Toast.LENGTH_SHORT
                ).show()
            }) {
        }
        queue.add(request)

    }



    fun guardarDatos(usuario : Usuario) {

        val admin = AdminSQLiteOpenHelper(this,"fichadodb", null, 1)
        val bd = admin.writableDatabase
        val registro = ContentValues()
        registro.put("idusuario", usuario.idusuario)
        registro.put("usuario", usuario.usuario)
        registro.put("password", usuario.password)
        registro.put("perfil", usuario.perfil)
        bd.insert("usuario", null, registro)
        bd.close()
    }

    fun verpass(view: View) {

        var txtcontra = findViewById<EditText>(R.id.txtPass)
        txtcontra.setInputType(145)

        var btnVer =  findViewById<ImageButton>(R.id.btn_ver_pass)
        btnVer.isVisible = false


        var btnOcultar =  findViewById<ImageButton>(R.id.btn_ocultar_pass)
        btnOcultar.isVisible = true

    }

    fun ocultarpass(view: View) {

        var txtcontra = findViewById<EditText>(R.id.txtPass)
        txtcontra.setInputType(129)

        var btnVer =  findViewById<ImageButton>(R.id.btn_ver_pass)
        btnVer.isVisible = true


        var btnOcultar =  findViewById<ImageButton>(R.id.btn_ocultar_pass)
        btnOcultar.isVisible = false

    }

}