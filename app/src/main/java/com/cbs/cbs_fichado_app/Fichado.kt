package com.cbs.cbs_fichado_app

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
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


class Fichado : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityFichadoBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFichadoBinding.inflate(layoutInflater)
        setContentView(binding.root)

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


        //Desabilito la seccion de fechas en el calendario


        val calendarView = findViewById<CalendarView>(R.id.calendarView)

        val selectedDate: Long = calendarView.getDate()

        calendarView.setMinDate(selectedDate)

        calendarView.setMaxDate(selectedDate)


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

                mostrarModal()

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }

    fun mostrarModal() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("FICHADO")

        val customLayout: View = layoutInflater
            .inflate(
                R.layout.modal_dni,
                null)
        builder.setView(customLayout)

        builder.setPositiveButton("REGISTRAR FICHARO", DialogInterface.OnClickListener { dialog, which ->

            val editText = customLayout.findViewById<EditText>(R.id.dni)

            var dni = editText.text.toString()
            validaPersonal(dni)
        })

        val dialog = builder.create()
        dialog.show()

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

                        val datoPersona = Intent(this, DatosPersonal::class.java)

                        datoPersona.putExtra("Persona", userData.nombreapellido)

                        startActivity(datoPersona)

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