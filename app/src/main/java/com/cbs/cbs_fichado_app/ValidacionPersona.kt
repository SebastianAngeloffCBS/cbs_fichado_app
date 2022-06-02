package com.cbs.cbs_fichado_app

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.cbs.cbs_fichado_app.databinding.ActivityValidacionPersonaBinding
import java.text.SimpleDateFormat
import java.util.*

class ValidacionPersona : AppCompatActivity() {

    private lateinit var binding: ActivityValidacionPersonaBinding

    private  var dimension : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityValidacionPersonaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        dimension = bundle?.getString("dimension").toString()
    }


    fun ingreso(view: View) {

        val fichado = Intent(this, Fichado::class.java)
        startActivity(fichado)



        //1 Obtengo el DNI
        var documento = binding.txtdni.text.toString()


        //2 Verificar que la persona este en la bd local
        val admin1 = AdminSQLiteOpenHelper(this, "administracion", null, 1)
        val bd1 = admin1.writableDatabase
        val fila1 = bd1.rawQuery("select * from persona where dni='${documento}'", null)
        bd1.close()

        var nombre : String = ""
        if (fila1.moveToFirst()){
            nombre = fila1.getString(1)
        }


        //Obtengo la fecha y la hora
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale("es", "ES"))
        sdf.timeZone = TimeZone.getTimeZone("UTC+1")
        val currentdate = sdf.format(Date())


        //3 Guardo el fichado en la base local
        val admin = AdminSQLiteOpenHelper(this,"administracion", null, 1)
        val bd = admin.writableDatabase
        val registro = ContentValues()
        registro.put("dimension", dimension)
        registro.put("nombre", nombre)
        registro.put("dni", documento)
        registro.put("fechahora", currentdate)
        registro.put("sincronizado", 0)
        bd.insert("fichado", null, registro)
        bd.close()

        //Si hay conexión a internet sincronizo


        //Redirijo al inicio

    }


}