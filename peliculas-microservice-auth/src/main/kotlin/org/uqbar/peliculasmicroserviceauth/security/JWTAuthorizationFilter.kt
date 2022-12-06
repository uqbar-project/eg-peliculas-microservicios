package org.uqbar.peliculasmicroserviceauth.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JWTAuthorizationFilter : OncePerRequestFilter() {

   @Autowired
   lateinit var tokenUtils: TokenUtils

   override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
      val bearerToken = request.getHeader("Authorization")
      if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
         val token = bearerToken.replace("Bearer ", "")
         val usernamePAT = tokenUtils.getAuthentication(token)
         SecurityContextHolder.getContext().authentication = usernamePAT
      }
      filterChain.doFilter(request, response)
   }


}