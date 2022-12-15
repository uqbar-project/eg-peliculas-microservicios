package org.uqbar.peliculasmicroserviceranking.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.uqbar.peliculasmicroserviceranking.domain.Pelicula
import org.uqbar.peliculasmicroserviceranking.repository.PeliculaRepository

@Service
@Transactional
class PeliculaService {

   @Autowired
   lateinit var peliculaRepository: PeliculaRepository

   @Autowired
   lateinit var tmdbService: TMDBService

   val logger = LoggerFactory.getLogger(PeliculaService::class.java)

   fun buscarPelicula(idTMDB: Number): Pelicula {
      logger.info("Buscamos película por id ${idTMDB}")
      var pelicula = peliculaRepository.findByIdTMDB(idTMDB).block()
      if (pelicula == null) {
         logger.info("No existe la película la vamos a buscar")
         pelicula = tmdbService.buscarPeliculaPorId(idTMDB)
         peliculaRepository.save(pelicula)
            .subscribe()
      }
      return pelicula
   }
}