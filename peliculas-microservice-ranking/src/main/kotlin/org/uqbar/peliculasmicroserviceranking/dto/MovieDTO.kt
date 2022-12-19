package org.uqbar.peliculasmicroserviceranking.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.uqbar.peliculasmicroserviceranking.domain.Genero
import org.uqbar.peliculasmicroserviceranking.domain.Pelicula
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
class PeliculasDTO {
   lateinit var results: List<PopularMovieDTO>
}

@JsonIgnoreProperties(ignoreUnknown = true)
abstract class AbstractMovieDTO {
   lateinit var id: Number
   lateinit var title: String
   lateinit var overview: String
   lateinit var original_language: String
   lateinit var release_date: String

   fun toPelicula(): Pelicula {
      val pelicula = Pelicula()
      pelicula.idTMDB = id
      pelicula.sinopsis = overview
      pelicula.titulo = title
      pelicula.idioma = original_language
      pelicula.fechaSalida = LocalDate.parse(release_date)
      extraToPelicula(pelicula)
      return pelicula
   }

   abstract fun extraToPelicula(pelicula: Pelicula)

}

@JsonIgnoreProperties(ignoreUnknown = true)
open class MovieDTO : AbstractMovieDTO() {
   lateinit var genres: List<GenreDTO>

   override fun extraToPelicula(pelicula: Pelicula) {
      pelicula.generos = genres.map { it.toGenero() }.toMutableList()
   }

   private fun getGeneros() = genres.map { it.toGenero() }.toMutableList()
}

open class PopularMovieDTO : AbstractMovieDTO() {
   lateinit var genre_ids: List<Number>

   companion object {
      var generos = mutableMapOf<Number, Genero>()
   }

   override fun extraToPelicula(pelicula: Pelicula) {
      pelicula.generos = genre_ids.map { generos.get(it)!! }.toMutableList()
   }

}

@JsonIgnoreProperties(ignoreUnknown = true)
class GenresDTO {
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
