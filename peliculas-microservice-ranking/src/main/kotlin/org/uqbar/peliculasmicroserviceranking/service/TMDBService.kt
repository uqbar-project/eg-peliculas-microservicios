package org.uqbar.peliculasmicroserviceranking.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.uqbar.peliculasmicroserviceranking.domain.Pelicula
import org.uqbar.peliculasmicroserviceranking.dto.MovieDTO
import org.uqbar.peliculasmicroserviceranking.dto.PeliculasDTO
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
class TMDBService {

   @Value("\${tmdb.api-key}")
   lateinit var apiKey: String

   @Transactional(readOnly = true)
   fun peliculasPopulares(): List<Pelicula> {
      val response = prepareTMDBResponse("https://api.themoviedb.org/3/movie/popular?api_key=10a56b1f0d4208a8609c58d9e4c6321d&language=en-US&page=1")
      val popularesDTO = ObjectMapper().readValue(response.body(), PeliculasDTO::class.java)
      return popularesDTO.results.map { it.toPelicula() }
   }


   @Transactional(readOnly = true)
   fun buscarPeliculaPorId(_idTMDB: Number): Pelicula {
      val response = prepareTMDBResponse("https://api.themoviedb.org/3/movie/${_idTMDB}?api_key=${apiKey}&language=en-US")
      val movieDTO = ObjectMapper().readValue(response.body(), MovieDTO::class.java)
      return movieDTO.toPelicula()
   }

   private fun prepareTMDBResponse(uri: String): HttpResponse<String> {
      val request =
         HttpRequest.newBuilder(URI.create(uri))
            .GET()
            .build()

      val client = HttpClient.newBuilder().build()
      return client.send(request, HttpResponse.BodyHandlers.ofString())
   }

}