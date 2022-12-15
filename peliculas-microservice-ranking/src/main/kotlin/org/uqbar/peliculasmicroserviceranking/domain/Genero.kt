package org.uqbar.peliculasmicroserviceranking.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Genero {

   @Id
   lateinit var id: String
   lateinit var descripcion: String

}
