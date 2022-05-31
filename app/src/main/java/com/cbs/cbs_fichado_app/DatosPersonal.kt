package com.cbs.cbs_fichado_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cbs.cbs_fichado_app.databinding.ActivityDatosPersonalBinding

class DatosPersonal : AppCompatActivity() {

    private lateinit var binding: ActivityDatosPersonalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDatosPersonalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        val dato = bundle?.getString("Persona")

        binding.txtNombrePersona.setText(dato)
    }







}