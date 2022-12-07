package org.uqbar.peliculasmicroserviceauth.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint

class BasicStatusEntryPoint : AuthenticationEntryPoint {
   override fun commence(
      request: HttpServletRequest?,
      response: HttpServletResponse?,
      authException: AuthenticationException?
   ) {
      authException?.printStackTrace()
   }

}
