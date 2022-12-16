package org.uqbar.peliculasmicroserviceranking.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.uqbar.peliculasmicroserviceranking.domain.Genero
import org.uqbar.peliculasmicroserviceranking.domain.Pelicula
import org.uqbar.peliculasmicroserviceranking.dto.MovieDTO
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.LocalDate

@Service
class TMDBService {

   @Value("\${tmdb.api-key}")
   lateinit var apiKey: String

   fun buscarPeliculaPorId(_idTMDB: Number): Pelicula {
      val request =
         HttpRequest.newBuilder(URI.create("https://api.themoviedb.org/3/movie/${_idTMDB}?api_key=${apiKey}&language=en-US"))
            .GET()
            .build()

      val client = HttpClient.newBuilder().build()
      val response = client.send(request, HttpResponse.BodyHandlers.ofString())

      val movieDTO = ObjectMapper().readValue(response.body(), MovieDTO::class.java)

      return Pelicula().apply {
         idTMDB = movieDTO.id
         sinopsis = movieDTO.overview
         titulo = movieDTO.title
         idioma = movieDTO.original_language
         generos = getGeneros(movieDTO)
         fechaSalida = LocalDate.parse(movieDTO.release_date)
      }
   }

   private fun getGeneros(movieDTO: MovieDTO) = movieDTO.genres.map { it.toGenero() }.toMutableList()
}