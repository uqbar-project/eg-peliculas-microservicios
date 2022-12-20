package org.uqbar.peliculasmicroserviceranking.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import org.uqbar.peliculasmicroserviceranking.domain.Pelicula
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface PeliculaRepository : ReactiveCrudRepository<Pelicula, String> {

   fun findByIdTMDB(idTMDB: Int): Mono<Pelicula?>

   fun findAllByOrderByVistasDesc(): Flux<Pelicula>

}