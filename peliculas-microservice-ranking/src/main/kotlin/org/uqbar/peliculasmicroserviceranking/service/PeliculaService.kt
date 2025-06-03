package org.uqbar.peliculasmicroserviceranking.service

import jakarta.transaction.Transactional
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

   @Transactional(value = Transactional.TxType.REQUIRED)
   fun buscarPelicula(_idTMDB: Int): Pelicula {
      logger.info("Buscando película con id TMDB = $_idTMDB")
      return peliculaRepository.findByIdTMDB(_idTMDB).orElseGet {
         // En caso de que no encontremos la película en nuestra base mongo traemos la información de TMDB
         // y guardamos la información.
         val pelicula = tmdbService.buscarPeliculaPorId(_idTMDB)
         peliculaRepository.save(pelicula)
      }
   }

   @Transactional(value = Transactional.TxType.REQUIRED)
   fun verPelicula(idTMDB: Int): Pelicula {
      logger.info("Visualizar película $idTMDB")
      val pelicula = buscarPelicula(idTMDB)
      pelicula.sumarVista()
      logger.info("Película tiene ${pelicula.vistas} vistas")
      return peliculaRepository.save(pelicula)
   }

   @Transactional(value = Transactional.TxType.NEVER)
   fun masVistas() = peliculaRepository.findAllByOrderByVistasDesc()

   @Transactional(value = Transactional.TxType.NEVER)
   fun mejorCalificadas() = peliculaRepository.findAllByOrderByCalificacionPromedioDesc()

   @Transactional(value = Transactional.TxType.REQUIRED)
   fun calificarPelicula(peliculaUpdate: CalificacionPelicula): Pelicula {
      val usuario = usuarioService.getUsuario(peliculaUpdate.usuario)
      val pelicula = buscarPelicula(peliculaUpdate.idTMDB)
      if (pelicula.calificadaPor(usuario)) throw BusinessException("El usuario ${usuario.nombre} ya calificó la película ${pelicula.titulo}")
      pelicula.calificar(usuario, peliculaUpdate.valoracion)
      return peliculaRepository.save(pelicula)
   }

}