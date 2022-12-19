package org.uqbar.peliculasmicroserviceranking.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.uqbar.peliculasmicroserviceranking.service.PeliculaService
import org.uqbar.peliculasmicroserviceranking.service.TMDBService
import reactor.core.publisher.Flux

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

//   @MutationMapping
//   fun verPelicula(@Argument idTMDB: Int) = peliculaService.verPelicula(idTMDB)

}