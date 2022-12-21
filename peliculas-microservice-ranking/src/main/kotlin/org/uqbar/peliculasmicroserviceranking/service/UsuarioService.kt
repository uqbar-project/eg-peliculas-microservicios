package org.uqbar.peliculasmicroserviceranking.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.uqbar.peliculasmicroserviceranking.exceptions.BusinessException
import org.uqbar.peliculasmicroserviceranking.domain.Usuario

@Service
class UsuarioService {

   @Value("\${auth.base-url}")
   lateinit var baseUrl: String

   lateinit var token: String

   fun authorize(_token: String): Boolean {
      token = _token
      val authRequest = RequestEntity.get("${baseUrl}/auth/validate")
         .headers(HttpHeaders().apply {
            setBearerAuth(token)
         })
         .build()
      val authResponse = RestTemplate().exchange(authRequest, String::class.java)
      return authResponse.body == "ok"
   }

   fun getUsuario(nombreUsuario: String): Usuario {
      val authRequest = RequestEntity.get("${baseUrl}/auth/users/$nombreUsuario")
         .headers(HttpHeaders().apply {
            // Asume que el token tiene que existir
            setBearerAuth(token)
         })
         .build()
      val usuarioResponse = RestTemplate().exchange(authRequest, Usuario::class.java)
      if (usuarioResponse.statusCode.is5xxServerError) throw BusinessException("Hubo error al buscar el usuario $nombreUsuario")
      if (usuarioResponse.body == null) throw BusinessException("No se encontró el usuario $nombreUsuario")
      return usuarioResponse.body!!
   }

}