package org.uqbar.peliculasmicroserviceauth.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.uqbar.peliculasmicroserviceauth.model.Usuario
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class UsuarioBaseDTO(
   val id: Long,
   val nombre: String,
   val deuda: BigDecimal,
   val ultimoPago: LocalDate?,
   @JsonIgnore
   val ultimoLogin: LocalDateTime?,
) {
   @JsonProperty("ultimoLogin")
   fun ultimoLoginAsString(): String? {
      val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS")
      return ultimoLogin?.format(formatter)
   }
}

fun Usuario.toUsuarioBaseDTO() = UsuarioBaseDTO(id!!, nombre, deuda(), ultimoPago()?.fechaPago, ultimoLogin)