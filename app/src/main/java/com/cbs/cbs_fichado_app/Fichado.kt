package com.cbs.cbs_fichado_app

import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
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

        val calendarView = findViewById<CalendarView>(R.id.calendarView)

        val selectedDate: Long = calendarView.getDate()

        calendarView.setMinDate(selectedDate)

        calendarView.setMaxDate(selectedDate)


        if (isDarkThemeOn()){

            var logocbs = findViewById<ImageView>(R.id.imageView2)
            logocbs.setImageResource(R.mipmap.logo)

        }


    }

    fun isDarkThemeOn(): Boolean{
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show()
            } else {

                cargarFichado(result.contents)

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.fichado, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_fichado)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun cargarFichado(data :String){

        val datosPersonal = Intent(this, ValidacionPersona::class.java)
        datosPersonal.putExtra("dimension", data)
        startActivity(datosPersonal)

    }

    fun reconfigurar(item: MenuItem) {

        //VALIDAR QUE NO AYAN DATOS SIN SINCRONIZAR CON EL SERVER
        val admin = AdminSQLiteOpenHelper(this, "fichadodb", null, 1)
        val bd = admin.writableDatabase
        val fila = bd.rawQuery("select * from fichado where sincronizado = 'no' ", null)

        if (fila.moveToFirst()) {

            bd.close()
            Toast.makeText(this, "TIENE DATOS PENDIENTES DE SINCRONIZAR", Toast.LENGTH_SHORT)
                .show()

        } else {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("ATENCIÓN")
            builder.setMessage("SE BORRARA EL USUARIO/A DE LA BASE DE DATOS. ¿ ESTA SEGURO/A?")
            builder.setIcon(android.R.drawable.ic_dialog_alert)

            builder.setPositiveButton("SI"){dialogInterface, which ->
                val admin = AdminSQLiteOpenHelper(this, "fichadodb", null, 1)
                val bd = admin.writableDatabase
                val base1 = bd.delete("usuario", null, null)
                val base2 = bd.delete("persona", null, null)
                val base3 = bd.delete("fichado", null, null)

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



    }

    fun leerqr(view: View) {




        IntentIntegrator(this).initiateScan()
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

                        val validarPersona = Intent(this, ValidacionPersona()::class.java)

                        validarPersona.putExtra("Persona", userData.nombreapellido)

                        startActivity(validarPersona)

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

    fun cargapersonal(item: MenuItem) {
        val listado = Intent(this, ListadoPersonal()::class.java)
        listado.putExtra("mensaje", "no")
        startActivity(listado)
    }

    fun listadofichados(item: MenuItem) {
        val listado = Intent(this, ListadoFichados()::class.java)
        startActivity(listado)
    }

    fun sincronizardatos(item: MenuItem) {
        val listado = Intent(this, Sincronizar()::class.java)
        startActivity(listado)
    }

}