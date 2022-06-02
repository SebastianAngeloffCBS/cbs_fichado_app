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

//        cargalistview()

//        val admin = AdminSQLiteOpenHelper(this, "administracion", null, 1)
//        val bd = admin.readableDatabase
//        val fila = bd.rawQuery("select * from fichado", null)
//
//
//
//        if (fila.moveToFirst()) {
//
//            val arrayValues: ArrayList<String> = ArrayList()
//
//            do {
//
//                arrayValues.add("DNI: "+fila.getString(0)+"\n"+
//                        "FECHA: "+fila.getString(1)+"\n")
//
//
//            } while (fila.moveToNext())
//
//            bd.close()
//
//            Toast.makeText(
//                this,
//                fila.getString(1),
//                Toast.LENGTH_SHORT
//            ).show()
//
//
//            val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, arrayValues)
//            binding.listadofichados.adapter = adapter
//
//
//        } else {
//
//            Toast.makeText(
//                this,
//                "Sin datos",
//                Toast.LENGTH_SHORT
//            ).show()
//        }


    }

    fun  cargalistview() {


    }



    fun volver(view: View) {
        val inicio = Intent(this, Fichado()::class.java)
        startActivity(inicio)
    }

}