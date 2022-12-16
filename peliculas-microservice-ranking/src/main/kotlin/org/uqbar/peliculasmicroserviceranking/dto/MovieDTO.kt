package org.uqbar.peliculasmicroserviceranking.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.uqbar.peliculasmicroserviceranking.domain.Genero

@JsonIgnoreProperties(ignoreUnknown = true)
class MovieDTO {
   lateinit var id: Number
   lateinit var title: String
   lateinit var overview: String
   lateinit var original_language: String
   lateinit var release_date: String
   lateinit var genres: List<GenreDTO>
}

@JsonIgnoreProperties(ignoreUnknown = true)
class GenreDTO {
   lateinit var id: Number
   lateinit var name: String

   fun toGenero(): Genero {
      val genero = Genero()
      genero.idOriginal = id
      genero.descripcion = name
      return genero
   }

}
