package org.uqbar.peliculasmicroserviceauth.service

import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.uqbar.peliculasmicroserviceauth.dto.*
import org.uqbar.peliculasmicroserviceauth.exceptions.BusinessException
import org.uqbar.peliculasmicroserviceauth.exceptions.CredencialesInvalidasException
import org.uqbar.peliculasmicroserviceauth.exceptions.NotFoundException
import org.uqbar.peliculasmicroserviceauth.model.Usuario
import org.uqbar.peliculasmicroserviceauth.repository.UsuarioRepository
import org.uqbar.peliculasmicroserviceauth.security.TokenUtils

@Service
@Transactional
class UsuarioService : UserDetailsService {

   val logger = LoggerFactory.getLogger(UsuarioService::class.java)

   @Autowired
   lateinit var usuarioRepository: UsuarioRepository

   @Autowired
   lateinit var tokenUtils: TokenUtils

   @Transactional(Transactional.TxType.REQUIRED)
   fun login(credenciales: CredencialesDTO) {
      val usuario = validarUsuario(credenciales.usuario)
      usuario.loguearse()
      usuario.validarCredenciales(credenciales.password)
   }

   fun validarUsuario(nombreUsuario: String) = usuarioRepository.findByNombre(nombreUsuario).orElseThrow { CredencialesInvalidasException() }

   @Transactional(Transactional.TxType.NEVER)
   fun usuarios() = usuarioRepository.findAll().map { usuario ->
      usuario.toUsuarioBaseDTO()
   }

   @Transactional(Transactional.TxType.REQUIRED)
   fun crearUsuario(credencialesDTO: CredencialesDTO): Usuario {
      if (usuarioRepository.findByNombre(credencialesDTO.usuario).isPresent) {
         throw BusinessException("Ya existe un usuario con ese nombre")
      }
      val usuario = Usuario().apply {
         nombre = credencialesDTO.usuario
         crearPassword(credencialesDTO.password)
         validar()
      }
      usuarioRepository.save(usuario)
      return usuario
   }

   @Transactional(Transactional.TxType.REQUIRED)
   fun eliminarUsuario(idUsuario: Long): Usuario {
      val usuario = getUsuarioPorId(idUsuario)
      usuarioRepository.delete(usuario)
      return usuario
   }

   // NOT_SUPPORTED permite que otro método en transacción lo llame y luego continúe con dicha transacción
   // con NEVER si intentamos llamar desde un método que tiene transacción a verUsuario, dispara una excepción
   @Transactional(Transactional.TxType.NOT_SUPPORTED)
   fun verUsuario(idUsuario: Long) = getUsuarioPorId(idUsuario)

   @Transactional(Transactional.TxType.REQUIRED)
   fun facturar(facturacionDTO: FacturacionDTO): Usuario {
      val usuario = getUsuario(facturacionDTO.nombreUsuario)
      usuario.facturar(facturacionDTO.monto)
      usuarioRepository.save(usuario)
      return usuario
   }

   @Transactional(Transactional.TxType.REQUIRED)
   fun pagar(pagoDTO: PagoDTO): Usuario {
      val usuario = getUsuario(pagoDTO.nombreUsuario)
      usuario.pagar(pagoDTO.idFactura)
      usuarioRepository.save(usuario)
      return usuario
   }

   override fun loadUserByUsername(username: String?): UserDetails {
      if (username == null) throw CredencialesInvalidasException()
      val usuario = getUsuario(username)
      logger.info("Usuario " + usuario.nombre + " - Roles: " + roleFor(username))
      return User(usuario.nombre, usuario.password, roleFor(username))
   }

   private fun roleFor(username: String) = if (username.lowercase() == "admin") listOf(SimpleGrantedAuthority("ROLE_ADMIN")) else listOf()

   private fun getUsuario(nombreUsuario: String) = usuarioRepository.findByNombre(nombreUsuario).orElseThrow { NotFoundException("No se encontró el usuario con el nombre $nombreUsuario") }

   private fun getUsuarioPorId(idUsuario: Long) = usuarioRepository.findById(idUsuario).orElseThrow { NotFoundException("No se encontró el usuario con el identificador $idUsuario") }

   // El efecto que tiene es simplemente devolver un ok si el filtro de JWT (JWTAuthorizationFilter) pasa
   fun validar(): String {
      return "ok"
   }

}
