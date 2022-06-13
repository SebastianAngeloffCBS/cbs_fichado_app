package com.cbs.cbs_fichado_app

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.cbs.cbs_fichado_app.databinding.ActivityValidacionPersonaBinding
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.*

class ValidacionPersona : AppCompatActivity() {

    private lateinit var binding: ActivityValidacionPersonaBinding

    private  var dimension : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityValidacionPersonaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        dimension = bundle?.getString("dimension").toString()
    }

//    fun ingreso(view: View) {
//        fichar("ingreso")
//    }
//
//    fun salir(view: View) {
//        fichar("salida")
//    }

    fun fichar(ciclo :String, documento:String){

        // 1 Obtengo el DNI
//        var documento = binding.txtdni.text.toString()


        // 2 Obtengo la persona
        //Si no existe le muestro un mensaje que tiene que cargar el dni
        val admin1 = AdminSQLiteOpenHelper(this, "dbfichado", null, 1)
        val bd1 = admin1.writableDatabase
        var queryApp : String = "select * from persona where dni='" + documento + "'"
        val fila1 = bd1.rawQuery(queryApp, null)
        var nombre : String = ""
        if (fila1.moveToFirst()){
            nombre = fila1.getString(1)
        }
        bd1.close()

        if (nombre == ""){


            showMessageBox("PERSONA NO REGISTRADA. DEBE REGISTRAR LA MISMA EN EL APP.")


        }else{

            // 3 Obtengo la fecha y la hora
            val myTimeZone = TimeZone.getTimeZone("America/Argentina/Buenos_Aires")
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
            simpleDateFormat.timeZone = myTimeZone
            val currentdate = simpleDateFormat.format(Date())


            //3 Guardo el fichado en la base local
            insertaDatoLocal(nombre,documento,currentdate,ciclo)

            //Si hay conexión a internet sincronizo
            if(isConnected(this)){
                //Conectado.
                insertaServidor(nombre,documento,currentdate,ciclo)


            }

            //Redirijo al inicio
            val nuevo = Intent(this, MainActivity()::class.java)
            startActivity(nuevo)
        }




    }

    fun insertaServidor(nombre:String,dni:String,fechahora:String,ciclo:String){

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

                        //Actualizo local
                        actualizaDatoLocal()

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

    fun insertaDatoLocal(nombre:String,dni:String,fechahora:String,ciclo:String){

        val admin = AdminSQLiteOpenHelper(this,"dbfichado", null, 1)
        val bd = admin.writableDatabase
        val registro = ContentValues()
        registro.put("dimension", dimension)
        registro.put("nombre", nombre)
        registro.put("dni", dni)
        registro.put("fechahora", fechahora)
        registro.put("ciclo",ciclo)
        registro.put("sincronizado","no")
        bd.insert("fichado", null, registro)
        bd.close()

    }

    fun actualizaDatoLocal(){

        //Obtengo el id del utlimo registro
        val admin1 = AdminSQLiteOpenHelper(this, "dbfichado", null, 1)
        val bd1 = admin1.writableDatabase
        var queryApp : String = "select * from fichado order by id desc LIMIT 1"
        val fila1 = bd1.rawQuery(queryApp, null)

        var IdUltimoRegistro : String = ""
        if (fila1.moveToFirst()){
            IdUltimoRegistro = fila1.getString(0)
        }
        bd1.close()

        //Update al registro
        val admin = AdminSQLiteOpenHelper(this, "dbfichado", null, 1)
        val bd = admin.writableDatabase
        val registro = ContentValues()
        registro.put("sincronizado", "si")
        val cant = bd.update("fichado", registro, "Id=${IdUltimoRegistro}", null)
        bd.close()

    }

    fun isConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                return true
//                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
//
//                    Toast.makeText(applicationContext,"NetworkCapabilities.TRANSPORT_CELLULAR", Toast.LENGTH_LONG).show()
//                    return true
//                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
//                    Toast.makeText(applicationContext,"NetworkCapabilities.TRANSPORT_WIFI", Toast.LENGTH_LONG).show()
//                    return true
//                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
//                    Toast.makeText(applicationContext,"NetworkCapabilities.TRANSPORT_ETHERNET", Toast.LENGTH_LONG).show()
//                    return true
//                }
            }
        }
        return false
    }


    fun showMessageBox(text: String){

        val builder = AlertDialog.Builder(this)
        builder.setTitle("ATENCIÓN")
        builder.setMessage(text)
        builder.setPositiveButton("Continuar") { dialog, which ->
            val nuevo = Intent(this, ListadoPersonal()::class.java)
            startActivity(nuevo)
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()

    }

    fun fichar(view: View) {

        //Realizo validaciones
        var documento = binding.txtdni.text.toString()

        if (documento.length < 7) {
            Toast.makeText(applicationContext,"Faltan números", Toast.LENGTH_LONG).show()
        }else{
            //Dependiendo del dni que ingreso, verifico, cual fue el último ciclo
            val admin = AdminSQLiteOpenHelper(this, "dbfichado", null, 1)
            val bd = admin.writableDatabase
            var queryApp : String = "select * from fichado where dni='" + documento + "'" + "order by id desc limit 1"
            val fila = bd.rawQuery(queryApp, null)
            if (fila.moveToFirst()){
                var ciclo = fila.getString(6)
                if (ciclo == "ingreso"){
                    fichar("salida",documento)
                }else{
                    fichar("ingreso",documento)
                }
            }else{
                fichar("ingreso",documento)
            }


        }



    }

}