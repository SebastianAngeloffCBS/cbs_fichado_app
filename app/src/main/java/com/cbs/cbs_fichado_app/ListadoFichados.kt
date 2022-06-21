package com.cbs.cbs_fichado_app

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.cbs.cbs_fichado_app.databinding.ActivityListadoFichadosBinding
import com.cbs.cbs_fichado_app.databinding.ActivityListadoPersonalBinding

class ListadoFichados : AppCompatActivity() {

    private lateinit var binding: ActivityListadoFichadosBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListadoFichadosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cargalistview()




    }

    fun  cargalistview() {

        val admin = AdminSQLiteOpenHelper(this, "fichadodb", null, 1)
        val bd = admin.writableDatabase
        val fila = bd.rawQuery("select * from fichado", null)

        if (fila.moveToFirst()) {

            val arrayValues: ArrayList<String> = ArrayList()

            do {

                arrayValues.add("DNI: "+fila.getString(1)+ " | FECHA "+fila.getString(3)
                        +"\n"+"PERSONA: "+fila.getString(2)+"\n"
                        +"CICLO: "+fila.getString(6)+ " | SINCRONIZADO: "+fila.getString(5)+"\n")

            } while (fila.moveToNext())


            val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, arrayValues)
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

}