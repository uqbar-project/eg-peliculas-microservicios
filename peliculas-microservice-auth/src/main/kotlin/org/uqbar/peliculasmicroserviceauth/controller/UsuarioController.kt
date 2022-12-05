package org.uqbar.peliculasmicroserviceauth.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.uqbar.peliculasmicroserviceauth.dto.CredencialesDTO
import org.uqbar.peliculasmicroserviceauth.dto.FacturacionDTO
import org.uqbar.peliculasmicroserviceauth.dto.PagoDTO
import org.uqbar.peliculasmicroserviceauth.model.Usuario
import org.uqbar.peliculasmicroserviceauth.service.UsuarioService

// https? armar el certificado
@RestController
@CrossOrigin(origins = ["**"])
@RequestMapping("/auth")
class UsuarioController {

   @Autowired
   lateinit var usuarioService: UsuarioService

   @PostMapping("/login")
   fun login(@RequestBody credencialesDTO: CredencialesDTO): String {
      usuarioService.login(credencialesDTO)
      // debería devolver un JWT con el rol, expiración, etc.
      // JWT
      // sin refresh token mediante servicio . es muy complicado
      return "ok"
   }

   @PostMapping("/user")
   fun crear(@RequestBody credencialesDTO: CredencialesDTO) = usuarioService.crearUsuario(credencialesDTO)

   @DeleteMapping("/user/{idUsuario}")
   fun eliminar(@PathVariable idUsuario: Long) = usuarioService.eliminarUsuario(idUsuario)

   @GetMapping("/users/{idUsuario}")
   fun usuarios(@PathVariable idUsuario: Long) = usuarioService.verUsuario(idUsuario)

   @GetMapping("/users")
   fun usuarios() = usuarioService.usuarios()

   @PatchMapping("/facturar")
   fun facturar(@RequestBody facturacionDTO: FacturacionDTO) = usuarioService.facturar(facturacionDTO)

   @PatchMapping("/pagar")
   fun facturar(@RequestBody pagoDTO: PagoDTO) = usuarioService.pagar(pagoDTO)

}