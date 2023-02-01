package org.uqbar.peliculasmicroserviceauth.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.uqbar.peliculasmicroserviceauth.exceptions.CredencialesInvalidasException
import org.uqbar.peliculasmicroserviceauth.service.UsuarioService

@Component
class JWTAuthorizationFilter : OncePerRequestFilter() {

   @Autowired
   lateinit var tokenUtils: TokenUtils

   @Autowired
   lateinit var usuarioService: UsuarioService

   override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
      val bearerToken = request.getHeader("Authorization")
      if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
         try {
            val token = bearerToken.replace("Bearer ", "")
            val usernamePAT = tokenUtils.getAuthentication(token)
            usuarioService.validarUsuario(usernamePAT.name)
            SecurityContextHolder.getContext().authentication = usernamePAT
            logger.info("username PAT: $usernamePAT")
         } catch (e: CredencialesInvalidasException) {
            response.status = HttpStatus.UNAUTHORIZED.value()
         }
      }
      filterChain.doFilter(request, response)
   }

}