package org.uqbar.peliculasmicroserviceranking.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.tomcat.util.json.JSONParser
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
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

   fun buscarPeliculaPorId(id: Number): Pelicula {
      val request = HttpRequest.newBuilder(URI.create("https://api.themoviedb.org/3/movie/popular?api_key=${apiKey}&language=en-US&page=1"))
         .GET()
         .build()

      val client = HttpClient.newBuilder().build()
      val response = client.send(request, HttpResponse.BodyHandlers.ofString())

      val movieDTO = ObjectMapper().readValue(response.body(), MovieDTO::class.java)
      val movie = movieDTO.results.first()

      return Pelicula().apply {
         idTMDB = movie.id
         sinopsis = movie.overview
         titulo = movie.title
         idioma = movie.original_language
         fechaSalida = LocalDate.parse(movie.release_date)
      }
   }
}