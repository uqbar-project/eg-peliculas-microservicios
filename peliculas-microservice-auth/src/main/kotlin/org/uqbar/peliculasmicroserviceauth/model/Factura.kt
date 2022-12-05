package org.uqbar.peliculasmicroserviceauth.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import java.math.BigDecimal
import java.time.LocalDate

@Entity
class Factura {
   @Id @GeneratedValue
   var id: Long? = null

   @Column(precision = 12, scale = 2)
   var importeFacturado = BigDecimal(0)

   var fechaFactura = LocalDate.now()

   var fechaPago: LocalDate? = null

   fun pagada() = fechaPago != null

   fun pagar() {
      fechaPago = LocalDate.now()
   }
}