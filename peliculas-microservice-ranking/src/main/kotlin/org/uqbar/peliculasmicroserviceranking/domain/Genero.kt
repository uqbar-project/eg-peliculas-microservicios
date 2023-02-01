package org.uqbar.peliculasmicroserviceranking.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Genero {

   @Id
   var id: String? = null
   lateinit var idOriginal: Number
   lateinit var descripcion: String

}
