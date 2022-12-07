package org.uqbar.peliculasmicroserviceauth.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.uqbar.peliculasmicroserviceauth.exceptions.BusinessException
import org.uqbar.peliculasmicroserviceauth.exceptions.CredencialesInvalidasException
import org.uqbar.peliculasmicroserviceauth.exceptions.NotFoundException
import java.math.BigDecimal

const val longitudMinimaPassword = 3

@Entity
class Usuario {
   @Id
   @GeneratedValue
   var id: Long? = null

   @Column(length = 255)
   var nombre = ""

   @JsonIgnore
   @Column(length = 255)
   var password = ""

   var activo = true

   @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
   @OrderColumn
   var facturas = mutableListOf<Factura>()

   fun ultimoPago() = facturas.lastOrNull { it.pagada() }

   fun deuda() = facturasImpagas().sumOf { it.importeFacturado }

   fun facturasImpagas() = facturas.filter { !it.pagada() }

   fun facturar(importe: BigDecimal) {
      facturas.add(Factura().apply {
         importeFacturado = importe
      })
   }

   fun pagar(idFactura: Long) {
      val factura = facturas.find { it.id == idFactura } ?: throw NotFoundException("La factura con identificador $idFactura no existe")
      factura.pagar()
   }

   // https://www.baeldung.com/java-password-hashing
   fun validarCredenciales(passwordAVerificar: String) {
      if (!getDefaultEncoder().matches(passwordAVerificar, password)) {
         throw CredencialesInvalidasException()
      }
   }

   private fun getDefaultEncoder(): PasswordEncoder {
      return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()!!
   }

   fun validar() {
      if (nombre.trim().isBlank()) {
         throw BusinessException("Debe ingresar nombre de usuario")
      }
      if (password.trim().isBlank()) {
         throw BusinessException("Debe ingresar contraseña")
      }
      if (password.length < longitudMinimaPassword) {
         throw BusinessException("La contraseña debe tener al menos $longitudMinimaPassword caracteres")
      }
   }

   fun crearPassword(rawPassword: String) {
      password = getDefaultEncoder().encode(rawPassword)
   }

   fun darDeBaja() {
      activo = false
   }
}