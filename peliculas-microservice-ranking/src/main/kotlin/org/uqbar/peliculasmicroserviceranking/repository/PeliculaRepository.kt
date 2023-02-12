package org.uqbar.peliculasmicroserviceranking.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.uqbar.peliculasmicroserviceranking.domain.Pelicula
import java.util.*

@Repository
interface PeliculaRepository : CrudRepository<Pelicula, String> {

   fun findByIdTMDB(idTMDB: Number): Optional<Pelicula>

   fun findAllByOrderByVistasDesc(): List<Pelicula>

   fun findAllByOrderByCalificacionPromedioDesc(): List<Pelicula>

}