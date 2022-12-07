package org.uqbar.peliculasmicroserviceauth.repository

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.uqbar.peliculasmicroserviceauth.model.Usuario
import java.util.*

interface UsuarioRepository : CrudRepository<Usuario, Long> {

   @Query("select u from Usuario u where u.activo = true and u.nombre = :nombre")
   fun findByNombre(nombre: String): Optional<Usuario>

   @EntityGraph(attributePaths = ["facturas"])
   override fun findAll(): List<Usuario>

   @Query("select u from Usuario u left join u.facturas where u.activo = true")
   fun findAllActivos(): List<Usuario>

   @EntityGraph(attributePaths = ["facturas"])
   override fun findById(idUsuario: Long): Optional<Usuario>
}