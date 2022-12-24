package org.uqbar.peliculasmicroserviceranking.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.uqbar.peliculasmicroserviceranking.domain.Pelicula
import org.uqbar.peliculasmicroserviceranking.exceptions.BusinessException
import org.uqbar.peliculasmicroserviceranking.graphql.CalificacionPelicula
import org.uqbar.peliculasmicroserviceranking.repository.PeliculaRepository
import reactor.core.publisher.Mono

@Service
class PeliculaService {

   @Autowired
   lateinit var peliculaRepository: PeliculaRepository

   @Autowired
   lateinit var tmdbService: TMDBService

   @Autowired
   lateinit var usuarioService: UsuarioService

   val logger: Logger = LoggerFactory.getLogger(PeliculaService::class.java)

   fun buscarPelicula(_idTMDB: Int): Mono<Pelicula?> {
      logger.info("Buscando película con id TMDB = $_idTMDB")
      return peliculaRepository.findByIdTMDB(_idTMDB).switchIfEmpty(
         // En caso de que no encontremos la película en nuestra base mongo traemos la información de TMDB
         // y guardamos la información. El método save de un ReactiveRepository devuelve un Mono<Pelicula>
         // esto significa que es un Future (una promesa de una película grabada como documento en la base)
         Mono.defer {
            val pelicula = tmdbService.buscarPeliculaPorId(_idTMDB)
            peliculaRepository.save(pelicula)
         })
   }

   fun verPelicula(idTMDB: Int): Mono<Pelicula> {
      logger.info("Visualizar película $idTMDB")
      return buscarPelicula(idTMDB).map { pelicula ->
         pelicula!!.sumarVista()
         pelicula
      }.flatMap { pelicula ->
         logger.info("Película tiene ${pelicula.vistas} vistas")
         // La llamada al subscribe es muy importante para que se dispare la actualización
         peliculaRepository.save(pelicula)
      }
   }

   fun masVistas() = peliculaRepository.findAllByOrderByVistasDesc()

   fun mejorCalificadas() = peliculaRepository.findAllByOrderByCalificacionPromedioDesc()

   fun calificarPelicula(peliculaUpdate: CalificacionPelicula): Mono<Pelicula> {
      val usuario = usuarioService.getUsuario(peliculaUpdate.usuario)
      return buscarPelicula(peliculaUpdate.idTMDB).flatMap { pelicula ->
         if (pelicula == null) return@flatMap Mono.error(BusinessException("La película ${peliculaUpdate.idTMDB} no fue encontrada en el sistema"))
         if (pelicula.calificadaPor(usuario)) return@flatMap Mono.error(BusinessException("El usuario ${usuario.nombre} ya calificó la película ${pelicula.titulo}"))
         pelicula.calificar(usuario, peliculaUpdate.valoracion)
         peliculaRepository.save(pelicula)
      }
   }

}