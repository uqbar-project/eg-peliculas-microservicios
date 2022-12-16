package org.uqbar.peliculasmicroserviceranking.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.uqbar.peliculasmicroserviceranking.service.PeliculaService

@RestController
@CrossOrigin(origins = ["**"])
@RequestMapping("/ranking")
class PeliculaController {

   @Autowired
   lateinit var peliculaService: PeliculaService

   @GetMapping("/pelicula/{idTMDB}")
   fun buscarPeliculaPorIdTMDB(@PathVariable idTMDB: Int) = peliculaService.buscarPelicula(idTMDB)

   @PatchMapping("/ver-pelicula/{idTMDB}")
   fun verPelicula(@PathVariable idTMDB: Int) = peliculaService.verPelicula(idTMDB)

}