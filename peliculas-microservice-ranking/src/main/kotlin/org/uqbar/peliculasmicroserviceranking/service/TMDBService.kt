package org.uqbar.peliculasmicroserviceranking.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.uqbar.peliculasmicroserviceranking.domain.Pelicula
import org.uqbar.peliculasmicroserviceranking.dto.*
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
class TMDBService {

   @Value("\${tmdb.api-key}")
   lateinit var apiKey: String

   @Value("\${tmdb.base-url}")
   lateinit var baseUrl: String

   @Transactional(readOnly = true)
   fun peliculasPopulares(): List<Pelicula> {
      actualizarGeneros()
      val response = prepareTMDBResponse("${baseUrl}/movie/popular?api_key=${apiKey}&language=en-US&page=1")
      val popularesDTO = ObjectMapper().readValue(response.body(), PeliculasDTO::class.java)
      return popularesDTO.results.map { it.toPelicula() }
   }


   @Transactional(readOnly = true)
   fun buscarPeliculaPorId(_idTMDB: Number): Pelicula {
      val response = prepareTMDBResponse("${baseUrl}/movie/${_idTMDB}?api_key=${apiKey}&language=en-US")
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

   private fun actualizarGeneros() {
      if (PopularMovieDTO.generos.isEmpty()) {
         val response =
            prepareTMDBResponse("${baseUrl}/genre/movie/list?api_key=${apiKey}&language=en-US")
         val result = ObjectMapper().readValue(response.body(), GenresDTO::class.java)
         PopularMovieDTO.generos = result.genres.associate {
            val genero = it.toGenero()
            genero.idOriginal to genero
         }.toMutableMap()
      }
   }
}