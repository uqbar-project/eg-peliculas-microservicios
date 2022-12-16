package org.uqbar.peliculasmicroserviceranking.repository

import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import org.uqbar.peliculasmicroserviceranking.domain.Pelicula
import reactor.core.publisher.Mono
import java.util.*

@Repository
interface PeliculaRepository : ReactiveCrudRepository<Pelicula, String> {

   fun findByIdTMDB(idTMDB: Int): Mono<Pelicula?>

}