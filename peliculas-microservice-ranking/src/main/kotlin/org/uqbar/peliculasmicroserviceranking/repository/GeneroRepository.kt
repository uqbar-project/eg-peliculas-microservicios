package org.uqbar.peliculasmicroserviceranking.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.uqbar.peliculasmicroserviceranking.domain.Genero

interface GeneroRepository : ReactiveCrudRepository<Genero, String> {
}