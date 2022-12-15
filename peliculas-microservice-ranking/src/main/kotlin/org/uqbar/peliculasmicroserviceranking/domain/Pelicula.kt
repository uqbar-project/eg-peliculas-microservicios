package org.uqbar.peliculasmicroserviceranking.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "pelicula")
class Pelicula {
   @Id
//   @JsonIgnore
   lateinit var id: String

   lateinit var idTMDB: Number
   lateinit var titulo: String

   var generos: MutableList<Genero> = mutableListOf()
   lateinit var idioma: String
   lateinit var sinopsis: String
   lateinit var fechaSalida: LocalDate

   var vistas = 0

//   @Embedded
   var calificaciones = mutableListOf<Calificacion>()

}
