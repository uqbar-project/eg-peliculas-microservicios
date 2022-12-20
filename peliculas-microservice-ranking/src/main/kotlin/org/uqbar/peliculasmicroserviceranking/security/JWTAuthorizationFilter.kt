package org.uqbar.peliculasmicroserviceranking.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JWTAuthorizationFilter : OncePerRequestFilter() {

   @Value("\${auth.base-url}")
   lateinit var baseUrl: String

   override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
      var authorized = false
      val bearerToken = request.getHeader("Authorization")
      if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
         val token = bearerToken.replace("Bearer ", "")
         val authRequest = RequestEntity.get("${baseUrl}/auth/validate")
            .headers(HttpHeaders().apply {
               setBearerAuth(token)
            })
            .build()
         val authResponse = RestTemplate().exchange(authRequest, String::class.java)
         authorized = authResponse.body == "ok"
      }
      if (authorized) {
         filterChain.doFilter(request, response)
      } else {
          response.status = 401
      }
   }

}