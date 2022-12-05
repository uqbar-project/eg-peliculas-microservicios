package org.uqbar.peliculasmicroserviceauth.service

import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
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
class UsuarioService {

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
      val usuario = getUsuario(idUsuario)
      usuarioRepository.delete(usuario)
      return usuario
   }

   @Transactional(Transactional.TxType.NEVER)
   fun verUsuario(idUsuario: Long): Usuario {
      return getUsuario(idUsuario)
   }

   @Transactional(Transactional.TxType.REQUIRED)
   fun facturar(facturacionDTO: FacturacionDTO): Usuario {
      val usuario = getUsuario(facturacionDTO.idUsuario)
      usuario.facturar(facturacionDTO.monto)
      usuarioRepository.save(usuario)
      return usuario
   }

   private fun getUsuario(idUsuario: Long) = usuarioRepository.findById(idUsuario).orElseThrow { NotFoundException("No se encontró el usuario con el identificador $idUsuario") }

   fun pagar(pagoDTO: PagoDTO): Usuario {
      val usuario = getUsuario(pagoDTO.idUsuario)
      usuario.pagar(pagoDTO.idFactura)
      usuarioRepository.save(usuario)
      return usuario
   }
}
