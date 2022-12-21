package org.uqbar.peliculasmicroserviceranking.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.data.annotation.Id

class Calificacion {

   @Id
   lateinit var id: String

   lateinit var usuario: Usuario
   lateinit var valoracion: Number

}

@JsonIgnoreProperties(ignoreUnknown = true)
class Usuario {
   lateinit var id: String
   lateinit var nombre: String
   var ultimoLogin: String? = null
}
