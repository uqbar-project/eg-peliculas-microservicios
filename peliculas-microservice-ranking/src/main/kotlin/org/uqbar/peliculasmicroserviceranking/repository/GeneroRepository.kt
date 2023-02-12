package org.uqbar.peliculasmicroserviceranking.repository

import org.springframework.data.repository.CrudRepository
import org.uqbar.peliculasmicroserviceranking.domain.Genero

interface GeneroRepository : CrudRepository<Genero, String> {
}