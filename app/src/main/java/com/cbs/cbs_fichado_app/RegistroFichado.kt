package com.cbs.cbs_fichado_app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.cbs.cbs_fichado_app.databinding.RegistroFichadoBinding

class RegistroFichado : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: RegistroFichadoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = RegistroFichadoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        val dato = bundle?.getString("UsuarioIntra")

        val txtUsuario = findViewById<TextView>(R.id.txtUsuario)
        txtUsuario.setText(dato)

        setSupportActionBar(binding.appBarRegistroFichado.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_registro_fichado)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_registro_fichado)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun reconfigurar(item: MenuItem) {

        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("ATENCIÓN")
        //set message for alert dialog
        builder.setMessage("SE BORRARA EL USUARIO/A DE LA BASE DE DATOS. ¿ ESTA SEGURO/A?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("SI"){dialogInterface, which ->
            val admin = AdminSQLiteOpenHelper(this, "administracion", null, 1)
            val bd = admin.writableDatabase
            val cant = bd.delete("usuario", null, null)
            bd.close()

            val fichado = Intent(this, MainActivity::class.java)

            startActivity(fichado)
        }
        //performing cancel action
//        builder.setNeutralButton("Cancel"){dialogInterface , which ->
//            Toast.makeText(applicationContext,"clicked cancel\n operation cancel",Toast.LENGTH_LONG).show()
//        }
        //performing negative action
        builder.setNegativeButton("NO"){dialogInterface, which ->
            Toast.makeText(applicationContext,"OPERACIÓN CANCELADA",Toast.LENGTH_LONG).show()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()





    }
}