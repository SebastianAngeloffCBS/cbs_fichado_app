package com.cbs.cbs_fichado_app

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.cbs.cbs_fichado_app.databinding.ActivityFichadoBinding
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.android.material.navigation.NavigationView
import com.google.zxing.integration.android.IntentIntegrator
import org.json.JSONException
import java.util.*
import kotlin.collections.HashMap


class Fichado : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityFichadoBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFichadoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        val dato = bundle?.getString("UsuarioIntra")

//        val txtUsuario = findViewById<TextView>(R.id.txtUsuario)
//        txtUsuario.setText(dato)

        setSupportActionBar(binding.appBarFichado.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_fichado)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)




    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.fichado, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_fichado)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun reconfigurar(item: MenuItem) {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("ATENCIÓN")
        builder.setMessage("SE BORRARA EL USUARIO/A DE LA BASE DE DATOS. ¿ ESTA SEGURO/A?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("SI"){dialogInterface, which ->
            val admin = AdminSQLiteOpenHelper(this, "administracion", null, 1)
            val bd = admin.writableDatabase
            val cant = bd.delete("usuario", null, null)
            bd.close()

            val fichado = Intent(this, MainActivity::class.java)

            startActivity(fichado)
        }

        builder.setNegativeButton("NO"){dialogInterface, which ->
            Toast.makeText(applicationContext,"OPERACIÓN CANCELADA", Toast.LENGTH_LONG).show()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

    }

    fun leerqr(view: View) {

        IntentIntegrator(this).initiateScan()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "El valor escaneado es: " + result.contents, Toast.LENGTH_LONG).show()


                //obtengo el texto devuelvo por el qr(Dimensión)
                //depliego un modal en donde el usuario ingresa el dni

                mostrarModal()



            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }

   fun mostrarModal() {

       val builder = AlertDialog.Builder(this)
       builder.setTitle("INGRESE SU DNI")

       // Set up the input
       val input = EditText(this)
       // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
       input.setHint("DNI")
       input.inputType = InputType.TYPE_CLASS_NUMBER
       builder.setView(input)

       // Set up the buttons
       builder.setPositiveButton("INGRESAR", DialogInterface.OnClickListener { dialog, which ->
           // Here you get get input text from the Edittext
           var dni = input.text.toString()
           validaPersonal(dni)
       })

       builder.setNegativeButton("CANCELAR", DialogInterface.OnClickListener { dialog, which ->
           dialog.cancel()
           Toast.makeText(this, "Fichado cancelado", Toast.LENGTH_LONG).show()
       })

       builder.show()

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
//                        guardarDatos(userData)
//
//                        val fichado = Intent(this, Fichado::class.java)
//
//                        fichado.putExtra("UsuarioIntra", userData.usuario)
//
//                        startActivity(fichado)
                        Toast.makeText(this, userData.nombreapellido, Toast.LENGTH_SHORT)
                            .show()
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

    fun soporte(item: MenuItem) {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("ATENCIÓN")
        builder.setMessage("Ante cualquier inconveniente comunicarse con el departamento de sistemas. Gracias")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setNegativeButton("Entendido"){dialogInterface, which ->
            Toast.makeText(applicationContext,"Gracias", Toast.LENGTH_LONG).show()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

    }




}