package com.cbs.cbs_fichado_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class Fichado : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fichado)

        val bundle = intent.extras
        val dato = bundle?.getString("UsuarioIntra")

        val txtUsuario = findViewById<TextView>(R.id.txtUsuario)
        txtUsuario.setText(dato)
    }
}