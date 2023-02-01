package org.uqbar.peliculasmicroserviceauth.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.uqbar.peliculasmicroserviceauth.dto.CredencialesDTO
import org.uqbar.peliculasmicroserviceauth.dto.FacturacionDTO
import org.uqbar.peliculasmicroserviceauth.dto.PagoDTO
import org.uqbar.peliculasmicroserviceauth.model.Usuario
import org.uqbar.peliculasmicroserviceauth.security.TokenUtils
import org.uqbar.peliculasmicroserviceauth.service.UsuarioService

// https? armar el certificado
@RestController
@CrossOrigin(origins = ["**"])
@RequestMapping("/auth")
class UsuarioController {

   @Autowired
   lateinit var usuarioService: UsuarioService

   @Autowired
   lateinit var tokenUtils: TokenUtils

   @PostMapping("/login")
   fun login(@RequestBody credencialesDTO: CredencialesDTO): String {
      usuarioService.login(credencialesDTO)
      return tokenUtils.createToken(credencialesDTO.usuario, credencialesDTO.password)!!
   }

   @PostMapping("/user")
   fun crear(@RequestBody credencialesDTO: CredencialesDTO) = usuarioService.crearUsuario(credencialesDTO)

   @DeleteMapping("/user/{idUsuario}")
   fun eliminar(@PathVariable idUsuario: Long) = usuarioService.eliminarUsuario(idUsuario)

   @GetMapping("/users/{nombreUsuario}")
   fun verUsuario(@PathVariable nombreUsuario: String) = usuarioService.verUsuario(nombreUsuario)

   @GetMapping("/users")
   fun usuarios() = usuarioService.usuarios()

   @PatchMapping("/invoice")
   fun facturar(@RequestBody facturacionDTO: FacturacionDTO) = usuarioService.facturar(facturacionDTO)

   @PatchMapping("/pay")
   fun pagar(@RequestBody pagoDTO: PagoDTO) = usuarioService.pagar(pagoDTO)

   @GetMapping("/validate")
   fun validar() = usuarioService.validar()

}