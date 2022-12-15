package org.uqbar.peliculasmicroserviceranking.domain

import org.springframework.data.annotation.Id
import java.math.BigDecimal

class Calificacion {

   @Id
   lateinit var id: String

   lateinit var usuario: String
   lateinit var valoracion: BigDecimal

}
