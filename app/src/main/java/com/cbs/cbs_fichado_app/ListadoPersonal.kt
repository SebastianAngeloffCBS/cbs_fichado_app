package com.cbs.cbs_fichado_app

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.cbs.cbs_fichado_app.databinding.ActivityListadoPersonalBinding

class ListadoPersonal : AppCompatActivity() {

    private lateinit var binding: ActivityListadoPersonalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListadoPersonalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        val dato = bundle?.getString("mensaje")

        if (dato == "si")
        {
            Toast.makeText(
                this,
                "Operación exitosa",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.contenidoprincipal.isVisible = false


       cargalistview()

    }

    fun  cargalistview() {

        val admin = AdminSQLiteOpenHelper(this, "fichadodb", null, 1)
        val bd = admin.writableDatabase
        val fila = bd.rawQuery("select * from persona", null)




        if (fila.moveToFirst()) {

            val arrayValues: ArrayList<String> = ArrayList()

            do {

                arrayValues.add("DNI: "+fila.getString(0)+"\n" + "NOMBRE: "+fila.getString(1)+"\n")


            } while (fila.moveToNext())

            bd.close()



            val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, arrayValues)
            binding.listadopersonal.adapter = adapter


        } else {

            Toast.makeText(
                this,
                "Sin datos",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun volver(view: View) {
        val inicio = Intent(this, Fichado()::class.java)
        startActivity(inicio)
    }

    fun cargarpersona(view: View) {
        val nuevo = Intent(this, CargaPersona()::class.java)
        startActivity(nuevo)
    }

    fun validarPass(view: View) {

        //Obtengo la contraseña cargada

        var sql = "select * from usuario where password = '" + binding.txtPass.text.toString() +"'"

        //La comparo con la de la bd
        val admin = AdminSQLiteOpenHelper(this, "fichadodb", null, 1)
        val bd = admin.writableDatabase
        val fila = bd.rawQuery(sql, null)
        if (fila.moveToFirst()) {

            bd.close()

            binding.contenidoprincipal.isVisible = true
            binding.contenedorvalidador.isVisible = false


        } else {
            Toast.makeText(this, "La contraseña no coincide con la del usuario supervisor que inicio sesión.", Toast.LENGTH_SHORT)
                .show()
            bd.close()
        }
        //si es true oculto el validador y muestro los datos

    }

    fun verpass(view: View) {

        var txtcontra = findViewById<EditText>(com.cbs.cbs_fichado_app.R.id.txtPass)
        txtcontra.setInputType(145)

        var btnVer =  findViewById<ImageButton>(com.cbs.cbs_fichado_app.R.id.btn_ver_pass)
        btnVer.isVisible = false


        var btnOcultar =  findViewById<ImageButton>(com.cbs.cbs_fichado_app.R.id.btn_ocultar_pass)
        btnOcultar.isVisible = true

    }

    fun ocultarpass(view: View) {

        var txtcontra = findViewById<EditText>(com.cbs.cbs_fichado_app.R.id.txtPass)
        txtcontra.setInputType(129)

        var btnVer =  findViewById<ImageButton>(com.cbs.cbs_fichado_app.R.id.btn_ver_pass)
        btnVer.isVisible = true


        var btnOcultar =  findViewById<ImageButton>(com.cbs.cbs_fichado_app.R.id.btn_ocultar_pass)
        btnOcultar.isVisible = false

    }

}