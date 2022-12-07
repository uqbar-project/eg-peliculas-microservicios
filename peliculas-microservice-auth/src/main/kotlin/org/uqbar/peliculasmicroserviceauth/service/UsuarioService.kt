package org.uqbar.peliculasmicroserviceauth.service

import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.uqbar.peliculasmicroserviceauth.dto.CredencialesDTO
import org.uqbar.peliculasmicroserviceauth.dto.FacturacionDTO
import org.uqbar.peliculasmicroserviceauth.dto.PagoDTO
import org.uqbar.peliculasmicroserviceauth.dto.UsuarioBaseDTO
import org.uqbar.peliculasmicroserviceauth.exceptions.CredencialesInvalidasException
import org.uqbar.peliculasmicroserviceauth.exceptions.NotFoundException
import org.uqbar.peliculasmicroserviceauth.model.Usuario
import org.uqbar.peliculasmicroserviceauth.repository.UsuarioRepository

@Service
@Transactional
class UsuarioService : UserDetailsService {

   @Autowired
   lateinit var usuarioRepository: UsuarioRepository

   @Transactional(Transactional.TxType.NEVER)
   fun login(credenciales: CredencialesDTO) {
      val usuario = usuarioRepository.findByNombre(credenciales.usuario).orElseThrow { CredencialesInvalidasException() }
      usuario.validarCredenciales(credenciales.password)
   }

   @Transactional(Transactional.TxType.NEVER)
   fun usuarios() = usuarioRepository.findAll().map { usuario ->
      UsuarioBaseDTO(usuario.id!!, usuario.nombre, usuario.deuda(), usuario.ultimoPago()?.fechaPago)
   }

   @Transactional(Transactional.TxType.NEVER)
   fun usuariosActivos() = usuarioRepository.findAllActivos().map { usuario ->
      UsuarioBaseDTO(usuario.id!!, usuario.nombre, usuario.deuda(), usuario.ultimoPago()?.fechaPago)
   }

   @Transactional(Transactional.TxType.REQUIRED)
   fun crearUsuario(credencialesDTO: CredencialesDTO): Usuario {
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
      usuario.darDeBaja()
      usuarioRepository.save(usuario)
      return usuario
   }

   @Transactional(Transactional.TxType.NEVER)
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
      return User(usuario.nombre, usuario.password, listOf())
   }

   private fun getUsuario(nombreUsuario: String) = usuarioRepository.findByNombre(nombreUsuario).orElseThrow { NotFoundException("No se encontró el usuario con el nombre $nombreUsuario") }

   private fun getUsuarioPorId(idUsuario: Long) = usuarioRepository.findById(idUsuario).orElseThrow { NotFoundException("No se encontró el usuario con el identificador $idUsuario") }

}
