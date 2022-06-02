package com.cbs.cbs_fichado_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cbs.cbs_fichado_app.databinding.ActivityValidacionPersonaBinding

class ValidacionPersona : AppCompatActivity() {

    private lateinit var binding: ActivityValidacionPersonaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityValidacionPersonaBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}