package com.cbs.cbs_fichado_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.cbs.cbs_fichado_app.databinding.ActivityListadoFichadosBinding
import com.cbs.cbs_fichado_app.databinding.ActivitySincronizarBinding

class Sincronizar : AppCompatActivity() {

    private lateinit var binding: ActivitySincronizarBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySincronizarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cargalistview()

    }

    fun  cargalistview() {

        val admin = AdminSQLiteOpenHelper(this, "dbfichado", null, 1)
        val bd = admin.writableDatabase
        val fila = bd.rawQuery("select * from fichado where sincronizado = 'no' ", null)

        if (fila.moveToFirst()) {

            val arrayValues: ArrayList<String> = ArrayList()

            do {

                arrayValues.add("DNI: "+fila.getString(1)+ " | FECHA "+fila.getString(3)
                        +"\n"+"PERSONA: "+fila.getString(2)+"\n"
                        +"CICLO: "+fila.getString(6)+ " | SINCRONIZADO: "+fila.getString(5)+"\n")

            } while (fila.moveToNext())


            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayValues)
            binding.listadofichados.adapter = adapter

            bd.close()


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

    fun sincronizar(view: View) {
        Toast.makeText(
            this,
            "EN DESARROLLO",
            Toast.LENGTH_SHORT
        ).show()
    }

}