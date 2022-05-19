package com.cbs.cbs_fichado_app

import com.fasterxml.jackson.annotation.JsonProperty

 data class Usuario (
     val idusuario : Long ,
     val usuario : String,
     val password: String,
     val perfil  : String
)


