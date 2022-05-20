package com.cbs.cbs_fichado_app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.json.JSONException


class MainActivity : AppCompatActivity() {

    //val app = applicationContext as UsuarioApp

    private var tienePermisoInternet = false
    private val CODIGO_PERMISOS_INTERNET = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Solicitar permisos acceso a internet

       




        //CONSULTO LOS DATOS EN SQLite SI EXISTEN REDIRIJO A LA PANTALLA DE FICHADO

        // var tieneuser = app.room.usuarioDao().getAll()
        // if (tieneuser == null){
        //       Toast.makeText(this, "No tiene user", Toast.LENGTH_SHORT).show()
        //    }else{
        //       Toast.makeText(this, "Tiene user", Toast.LENGTH_SHORT).show()
        //
        //   }


    }






    fun conectar(view: View) {

        //Verifico la conexión a internet

        var txtUser  = findViewById<EditText>(R.id.txtUser).text.toString()
        var txtPass  = findViewById<EditText>(R.id.txtPass).text.toString()
         sendPost(txtUser, txtPass)

    }

    //    fun guardarDatos(usuario : Usuario) {
    //
    //        var user = UsuarioEntity(0,usuario.idusuario,usuario.usuario,usuario.password,usuario.perfil)
    //
    //        app.room.usuarioDao().insert(user)
    //    }

    fun sendPost(txtUser: String, txtPass: String) {

        val url = "http://ws.grupocbs.com.ar/api/Telefonos/VerificaUsePass"

        val queue = Volley.newRequestQueue(this@MainActivity)

        val request: StringRequest = object : StringRequest(
            Method.POST,
            url,
            Response.Listener { response ->
                try
                {
                    val mapper = jacksonObjectMapper()

                    val usuario : Usuario = mapper.readValue(response)

//                    guardarDatos(usuario)

                    val fichado = Intent(this, Fichado::class.java)

                    fichado.putExtra("UsuarioIntra", usuario.usuario)

                    startActivity(fichado)
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

    //Recorrer el json de a 1 dato
    // val respObj = JSONObject(response)
    // var idusuario = respObj.getString("idusuario")
    // var usuario = respObj.getString("usuario")
    // var password = respObj.getString("password")
    // var perfil = respObj.getString("perfil")

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

}