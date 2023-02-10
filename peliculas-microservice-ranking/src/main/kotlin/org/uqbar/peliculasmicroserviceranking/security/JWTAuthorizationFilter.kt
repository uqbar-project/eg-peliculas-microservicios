package org.uqbar.peliculasmicroserviceranking.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.uqbar.peliculasmicroserviceranking.service.UsuarioService

@Component
class JWTAuthorizationFilter : OncePerRequestFilter() {

   @Autowired
   lateinit var usuarioService: UsuarioService

   override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
      var authorized = false
      val bearerToken = request.getHeader("Authorization")
      if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
         val token = bearerToken.replace("Bearer ", "")
         authorized = usuarioService.authorize(token)
      }

      logger.warn("bearer token $bearerToken")
      logger.warn("authorized $authorized")

      if (authorized) {
         filterChain.doFilter(request, response)
      } else {
          response.status = 401
      }
   }

}