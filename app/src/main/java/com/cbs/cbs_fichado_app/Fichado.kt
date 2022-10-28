package com.cbs.cbs_fichado_app

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import java.util.jar.Manifest


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
        /**CUANDO SE CARGUE LA INSTANCIA LE SOLICITO LOS PERMISOS*/

        enableLocation()

        /*--------------------------------------*/
       val builder = androidx.appcompat.app.AlertDialog.Builder(this)
       /* builder.setTitle("Fichado APP")
       builder.setMessage(
           "- Accede a tu ubicación para la monitorización de la nomina de la empresa aunque la aplicación no se esté usando \n - Recuerde tener habilitada su ubicacion")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setNegativeButton("✔ OK")*/

            val datosPersonal = Intent(this, ValidacionPersona::class.java)
            datosPersonal.putExtra("dimension", data)
           startActivity(datosPersonal)


       val alertDialog: androidx.appcompat.app.AlertDialog = builder.create()
       alertDialog.setCancelable(false)
       alertDialog.show()


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

    /*TODO*PERMISOS APPP*/

    /**esta dado el permiso o no?*/
    private fun isPermissionsGranted() = ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED


    companion object {
        const val REQUEST_CODE_LOCATION = 0
        var isMyLocationEnabled =false
    }
    /*si no esta dado */
    private fun requestLocationPermission() {
        /*SI LE MOSTRAMOS ANTES LOS PERMISOS Y LOS RECHAZO */
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
               android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "No podemos monitorizar la nonima sin acceso a su ubicacion", Toast.LENGTH_SHORT).show()
        } else {
            /*QUE LOS SOLICITE*/
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION)
        }
    }
    private fun enableLocation(){
        if(isPermissionsGranted()){
            isMyLocationEnabled=true
        }else{
            requestLocationPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when(requestCode){
            REQUEST_CODE_LOCATION -> if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                isMyLocationEnabled = true
            }else{
                Toast.makeText(this, "Para activar la localización ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }
}
