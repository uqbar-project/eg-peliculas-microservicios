package org.uqbar.peliculasmicroserviceauth.repository

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.repository.CrudRepository
import org.uqbar.peliculasmicroserviceauth.model.Usuario
import java.util.*

interface UsuarioRepository : CrudRepository<Usuario, Long> {

   fun findByNombre(nombre: String): Optional<Usuario>

   @EntityGraph(attributePaths = ["facturas"])
   override fun findAll(): List<Usuario>
}