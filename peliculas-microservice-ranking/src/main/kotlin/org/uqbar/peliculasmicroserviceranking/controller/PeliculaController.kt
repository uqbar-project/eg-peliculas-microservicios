package org.uqbar.peliculasmicroserviceranking.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.uqbar.peliculasmicroserviceranking.domain.Pelicula
import org.uqbar.peliculasmicroserviceranking.graphql.CalificacionPelicula
import org.uqbar.peliculasmicroserviceranking.service.PeliculaService
import org.uqbar.peliculasmicroserviceranking.service.TMDBService
import reactor.core.publisher.Mono

// https://opensource.expediagroup.com/graphql-kotlin/docs/

@Controller
@CrossOrigin(origins = ["**"])
class PeliculaController {

   @Autowired
   lateinit var peliculaService: PeliculaService

   @Autowired
   lateinit var tmdbService: TMDBService

   @QueryMapping
   fun peliculaPorIdTMDB(@Argument idTMDB: Int) = peliculaService.buscarPelicula(idTMDB)

   @QueryMapping
   fun populares() = tmdbService.peliculasPopulares()

   @QueryMapping
   fun masVistas() = peliculaService.masVistas()

   @MutationMapping
   fun verPelicula(@Argument idTMDB: Int) = peliculaService.verPelicula(idTMDB)

   @MutationMapping
   fun calificarPelicula(@Argument calificacionPelicula: CalificacionPelicula): Mono<Pelicula> {
      // TODO: try/catch
      return peliculaService.calificarPelicula(calificacionPelicula)
   }
}