package org.uqbar.peliculasmicroserviceranking.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.uqbar.peliculasmicroserviceranking.domain.Pelicula
import org.uqbar.peliculasmicroserviceranking.dto.*
import org.uqbar.peliculasmicroserviceranking.exceptions.BusinessException
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

   val logger: Logger = LoggerFactory.getLogger(TMDBService::class.java)

   fun peliculasPopulares(): List<Pelicula> {
      actualizarGeneros()
      val response = prepareTMDBResponse("${baseUrl}/movie/popular?api_key=${apiKey}&language=en-US&page=1")
      val popularesDTO = ObjectMapper().readValue(response.body(), PeliculasDTO::class.java)
      return popularesDTO.results.map { it.toPelicula() }
   }

   fun buscarPeliculaPorId(_idTMDB: Number): Pelicula {
      logger.info("buscando película con id $_idTMDB en TMDB")
      val response = prepareTMDBResponse("${baseUrl}/movie/${_idTMDB}?api_key=${apiKey}&language=en-US")
      logger.info("respuesta ${response.statusCode()} - ${response.body().toString()}")
      // TODO: qué pasa si no la encuentra
      // Recibimos un
      /*
      {
          "success": false,
          "status_code": 34,
          "status_message": "The resource you requested could not be found."
      }
       */
      if (response.statusCode() == 404) {
         throw BusinessException("No se encuentra la película cuyo id es $_idTMDB")
      }
      val movieDTO = ObjectMapper().readValue(response.body(), MovieDTO::class.java)
      return movieDTO.toPelicula()
   }

   private fun prepareTMDBResponse(uri: String): HttpResponse<String> {
      val request =
         HttpRequest.newBuilder(URI.create(uri))
            .GET()
            .build()

      logger.info("request ${request.uri()}")

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
