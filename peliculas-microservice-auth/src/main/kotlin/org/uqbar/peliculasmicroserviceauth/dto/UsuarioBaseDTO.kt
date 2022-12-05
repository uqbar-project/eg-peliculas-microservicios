package org.uqbar.peliculasmicroserviceauth.dto

import java.math.BigDecimal
import java.time.LocalDate

data class UsuarioBaseDTO(
   val id: Long,
   val nombre: String,
   val deuda: BigDecimal,
   val ultimoPago: LocalDate?
)