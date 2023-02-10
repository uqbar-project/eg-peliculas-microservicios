package org.uqbar.peliculasmicroserviceranking.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.uqbar.peliculasmicroserviceranking.domain.Usuario
import org.uqbar.peliculasmicroserviceranking.exceptions.BusinessException

@Service
class UsuarioService {

   @Value("\${auth.base-url}")
   lateinit var authBaseUrl: String

   lateinit var token: String

   val logger: Logger = LoggerFactory.getLogger(UsuarioService::class.java)

   fun authorize(_token: String): Boolean {
      token = _token
      logger.info("token $token")
      val authRequest = RequestEntity.get("${authBaseUrl}/auth/validate")
         .headers(HttpHeaders().apply {
            setBearerAuth(token)
         })
         .build()
      return try {
         val authResponse = RestTemplate().exchange(authRequest, String::class.java)
         logger.info("authorization passed: ${authResponse.body}")
         authResponse.body == "ok"
      } catch (e: Exception) {
         logger.info("authorization failed: ${e.message}")
         false
      }
   }

   fun getUsuario(nombreUsuario: String): Usuario {
      val authRequest = RequestEntity.get("${authBaseUrl}/auth/users/$nombreUsuario")
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