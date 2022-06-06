package com.cbs.cbs_fichado_app

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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


       cargalistview()

    }

    fun  cargalistview() {

        val admin = AdminSQLiteOpenHelper(this, "dbfichado", null, 1)
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
}